import { LitElement, html, css } from 'lit';
import {property, customElement} from 'lit/decorators.js';
import * as echarts from 'echarts/dist/echarts.min';

export class SOChart extends LitElement {

    static get properties() {
        return {
            minw: { type: String },
            maxw: { type: String },
            minh: { type: String },
            maxh: { type: String },
            width: { type: String },
            height: { type: String }
        };
    }
    render() {
        return html`<div id="${this.idChart}"
                         style="min-width:${this.minw};max-width:${this.maxw};width:${this.width};min-height:${this.minh};max-height:${this.maxh};height:${this.height};">
        </div>`;
    }

    constructor() {
        super();
        this.idChart = null;
        this.data = {};
        this.allOptions = "";
        this.minw = "5vw";
        this.minh = "5vw";
        this.maxw = "6000px";
        this.maxh = "6000px";
        this.width = "33vw";
        this.height = "33vh";
        this.events = [];
        
        // Keep a reference to pending resize event. We use this to not trigger too many render events on continuous resize
        this.pendingResizeEvent = null;
        this.resizeListener = (event) => {
            if(this.chart && this.chart != null) {
                // Clear previous pending event (if any) and register a new one. This way only last one will trigger
                if (this.pendingResizeEvent) {
                    clearTimeout(this.pendingResizeEvent);
                }
                this.pendingResizeEvent = setTimeout(() => {
                    this.chart.resize();
                    this.pendingResizeEvent = null;
                }, 500);
            }
        };
    }

    addEvent(event, data) {
        this.events.push({ event, data })
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
        window.addEventListener("resize", this.resizeListener);
    }

    disconnectedCallback() {
        window.removeEventListener("resize", this.resizeListener);
        super.disconnectedCallback();
        this.destroyChart();
    }

    updated(changedProps) {
        if(this.chart && this.chart != null && !changedProps.has("idChart")) {
            this.chart.resize();
        }
    }

    clearChart() {
        if(this.chart == null) {
            return;
        }
        this.chart.clear();
    }

    destroyChart() {
        if(this.chart == null) {
            return;
        }
        this.chart.dispose();
        this.chart = null;
    }

    setTheme(theme) {
        this.destroyChart();
        this.updateChart(false, this.allOptions, theme);
    }

    updateChart(full, options, theme) {
        if(full) {
            this.allOptions = options;
        }
        var json = JSON.parse(options);
        this._stuff(json);
        if(!this.chart || this.chart == null) {
            this.chart = echarts.init(this.shadowRoot.getElementById(this.idChart), theme);
        }
        this.events.forEach(event => {
            this.chart.on(event.event, { name: event.data }, params => {
               this.$server.runEvent(event.event, event.data);
           })
        });
        this.chart.setOption(json);
    }

    clearData() {
        this.data = {};
    }

    updateData(serial, data) {
        if(typeof serial !== 'undefined') {
            this.initData(serial, data);
        }
        var json = JSON.parse(this.allOptions);
        this._stuff(json);
        this.chart.setOption(json);
    }

    initData(serial, data) {
        this.data["d" + serial] = JSON.parse(data)["d"];
    }

    pushData(data) {
        data = JSON.parse(data)["d"];
        for(let k in data) {
            this.data[k].push(data[k]);
            this.data[k].shift();
        }
        this.updateData();
    }

    appendData(data) {
        data = JSON.parse(data)["d"];
        for(let k in data) {
            this.data[k].push(data[k]);
        }
        this.updateData();
    }

    resetData(serials) {
        serials = JSON.parse(serials)["d"];
        serials.forEach(v => {
            this.data["d" + v] = [];
        });
        this.updateData();
    }

    _stuff(obj) {
        this._stuffData(obj);
        this._stuffDataSet(obj);
        this._stuffFunc(obj);
        this._stuffFuncP(obj);
        this._stuffRenderer(obj);
    }

    _stuffFunc(obj) {
        var o;
        for(let k in obj) {
            o = obj[k];
            if(typeof o === 'object') {
                if(typeof o.function === 'object') {
                    obj[k] = new Function(o.function.params, o.function.body);
                } else {
                    this._stuffFunc(o);
                }
            }
        }
    }

    _stuffFuncP(obj) {
        var o;
        for(let k in obj) {
            o = obj[k];
            if(typeof o === 'object') {
                if(typeof o.functionP === 'object') {
                    obj[k] = p => this._formatter(p, o.functionP.body);
                } else {
                    this._stuffFuncP(o);
                }
            }
        }
    }

    _stuffRenderer(obj) {
        var o;
        for(let k in obj) {
            o = obj[k];
            if(k === "renderItem") {
                obj[k] = this._renderer(o);
            } else if(typeof o === 'object') {
                this._stuffRenderer(o);
            }
        }
    }

    _stuffData(obj) {
        var o;
        for(let k in obj) {
            o = obj[k];
            if(k === "data") {
                if(Array.isArray(o)) {
                    o.forEach((v, i, a) => {
                        a[i] = this.data["d" + v];
                    });
                } else {
                    obj[k] = this.data["d" + o];
                }
            } else if(typeof o === 'object') {
                this._stuffData(o);
            }
        }
    }

    _stuffDataSet(obj) {
        obj = obj.dataset;
        for(let k in obj.source) {
            obj.source[k] = this.data["d" + obj.source[k]];
        }
    }

    _formatter(p, v) {
        let s = "", len = v.length;
        for(let i = 0; i < len; i++) {
            let o = v[i];
            if(typeof o === 'string') {
                s = s + o;
            } else {
                let k = p.dataIndex;
                if(k == null) {
                    k = p[0].dataIndex;
                    if(k == null) {
                        continue;
                    }
                }
                let d;
                if(typeof o === 'object') {
                    d = this.data["d" + o[0]];
                    if(d == null) {
                        continue;
                    }
                    d = d[k];
                    if(d == null) {
                        continue;
                    }
                    d = d[o[1]];
                    if(d == null) {
                        continue;
                    }
                    s = s + d;
                } else {
                    d = this.data["d" + o];
                    if(d == null) {
                        continue;
                    }
                    d = d[k];
                    if(d == null) {
                        continue;
                    }
                    s = s + d;
                }
            }
        }
        return s;
    }

    // Get a renderer method
    _renderer(name) {
        return Reflect.get(this, "_render" + name);
    }

    // Built-in custom renderers

    // Horizontal bar renderer (used by Gantt chart).
    // Data Point: [Y-value, label, start, end, completed, bar color, prefix color, non-progress color]
    _renderHBar(params, api) {
        var HEIGHT_RATIO = 0.6;
        var index = api.value(0);
        var label = api.value(1);
        var start = api.coord([api.value(2), index]);
        var end = api.coord([api.value(3), index]);
        var donePercentage = api.value(4)
        var color = api.value(5);
        var prefixColor = api.value(6);
        var nonProgressColor = api.value(7);
        var barWidth = end[0] - start[0];
        var barHeight = api.size([0, 1])[1];
        var shiftY = 0;
        if(color === prefixColor) { // Not a Gantt chart
            shiftY = 0;
        } else {
            shiftY = -(barHeight / 2);
        }
        barHeight *= HEIGHT_RATIO;
        var x = start[0];
        var y = start[1] - (barHeight / 2) + shiftY;
        x += 3;
        barWidth -= 3;
        if(barWidth < 0) {
            barWidth = 0;
        }
        var rectSystem = {
            x: params.coordSys.x,
            y: params.coordSys.y,
            width: params.coordSys.width,
            height: params.coordSys.height
        };
        var rectPrefix = {
            x: x - 3,
            y: y,
            width: 3,
            height: barHeight
        };
        rectPrefix = echarts.graphic.clipRectByRect(rectPrefix, rectSystem);
        var rectBox = {
            x: x,
            y: y,
            width: barWidth,
            height: barHeight
        };
        rectBox = echarts.graphic.clipRectByRect(rectBox, rectSystem);
        var rectPercent;
        if(donePercentage >= 0 && donePercentage < 100) {
            var completedWidth = (barWidth + 3) * donePercentage / 100;
            rectPercent = {
                x: x + completedWidth - 3,
                y: y,
                width: barWidth - completedWidth + 3,
                height: barHeight / 3
            };
            rectPercent = echarts.graphic.clipRectByRect(rectPercent, rectSystem);
        }
        var hideText;
        if(shiftY == 0) { // Not a Gantt chart
            hideText = !rectPrefix;
        } else {
            hideText = !rectBox;
        }
        return {
            type: 'group',
            children: [{
                type: 'rect',
                ignore: !rectPrefix,
                shape: rectPrefix,
                z2: 1,
                style: api.style({
                    fill: prefixColor
                })
            }, {
                type: 'rect',
                ignore: !rectBox,
                shape: rectBox,
                z2: 1,
                style: api.style({
                    fill: color
                })
            }, {
                type: 'rect',
                ignore: !rectPercent,
                shape: rectPercent,
                z2: 2,
                style: api.style({
                    fill: nonProgressColor,
                    stroke: 'transparent',
                })
            }, {
                type: 'text',
                ignore: hideText,
                z2: 2,
                style: {
                    x: x,
                    y: y + (barHeight / 2),
                    fill: 'white',
                    stroke: 'black',
                    lineWidth: 3,
                    text: label,
                    textVerticalAlign: 'middle',
                    textAlign: 'left',
                    font: 'bold 12px sans-serif'
                }
            }]
        };
    }

    // Render labels on vertical axis - labels can be connected within the same group too (used by Gantt chart)
    // Data Point: [Y-value, group label, connected, label, sub-label, bar color]
    _renderVAxisLabel(params, api) {
        const minY = params.coordSys.y;
        const maxY = minY + params.coordSys.height;
        const width = params.coordSys.x - 15;
        var index = api.value(0);
        var groupName = api.value(1);
        var connected = api.value(2);
        var label = api.value(3);
        var sublabel = api.value(4);
        var color = api.value(5);
        var barHeight = api.size([0, 1])[1];
        var y = api.coord([0, index])[1] - barHeight;
        let ry = y + 0.2 * barHeight, ty1 = y + 0.6 * barHeight, ty2 = y + 0.8 * barHeight, rh = 0.8 * barHeight;
        if(ry < minY) {
            let d = minY - ry;
            ry = minY;
            rh -= d;
        }
        var rect = {
           x: 10,
           y: ry,
           width: width - 10,
           height: rh
        };
        let elements = {
            type: 'group',
            silent: true,
            children: [{
                type: 'rect', // Top curtain
                z2: 1,
                shape: {
                  x: 0,
                  y: 0,
                  width: params.coordSys.x + params.coordSys.width,
                  height: minY
                },
                style: {
                    fill: '#FFFFFF'
                }
            }, {
                type: 'rect', // Bottom curtain
                z2: 1,
                shape: {
                  x: 0,
                  y: params.coordSys.y + params.coordSys.height,
                  width: params.coordSys.x + params.coordSys.width,
                  height: 100
                },
                style: {
                    fill: '#FFFFFF'
                }
            }, {
                type: 'rect', // Left curtain
                z2: 1,
                shape: {
                  x: 0,
                  y: 0,
                  width: width + 15,
                  height: minY + params.coordSys.height
                },
                style: {
                    fill: '#FFFFFF'
                }
            }, {
                type: 'rect', // Right curtain
                z2: 1,
                shape: {
                  x: params.coordSys.x + params.coordSys.width,
                  y: 0,
                  width: 100,
                  height: minY + params.coordSys.height
                },
                style: {
                    fill: '#FFFFFF'
                }
            }, {
                type: 'rect',
                z2: 2,
                shape: rect,
                style: {
                    fill: color
                }
            }, {
                type: 'text',
                z2: 2,
                style: {
                    x: 15,
                    y: ty1,
                    text: label,
                    textVerticalAlign: 'bottom',
                    textAlign: 'left',
                    textFill: '#FFFFFF',
                    font: 'bold 12px/1 sans-serif'
                }
            }, {
                type: 'text',
                z2: 2,
                style: {
                    x: 15,
                    y: ty2,
                    textVerticalAlign: 'bottom',
                    textAlign: 'left',
                    text: sublabel,
                    textFill: '#FFFFFF',
                    font: 'bold 9px/1 sans-serif'
                }
            }]
        };
        if(connected == 1) {
            rect = {
               x: 10,
               y: y,
               width: 10,
               height: 0.2 * barHeight
            };
            elements.children.push({
                type: 'rect',
                z2: 2,
                ignore: y < minY,
                shape: rect,
                style: {
                    fill: color,
                }
            })
        } else {
            elements.children.push({
                type: 'text',
                z2: 2,
                ignore: (y + 1) < minY,
                style: {
                    x: 0,
                    y: ry + 3,
                    text: groupName,
                    fill: 'white',
                    stroke: 'black',
                    lineWidth: 3,
                    font: 'bold 12px/1 sans-serif',
                    textVerticalAlign: 'bottom',
                    textAlign: 'left'
                }
            })
        }
        return elements;
    }

    // Render arrow lines to show dependencies between bars rendered by the "HBar" renderer. (Used by Gantt chart)
    // Data Point: [Y-value, start, end, "{d:[[dependent Y-value, dependent start, dependent end]...]}"]
    _renderDependency(params, api) {
        const minX = params.coordSys.x;
        const minY = params.coordSys.y;
        const maxX = minX + params.coordSys.width;
        const maxY = minY + params.coordSys.height;
        const ignore = (x, y) => x < minX || x > maxX || y < minY || y > maxY;
        var HEIGHT_RATIO = 0.6;
        var index = api.value(0);
        var start = api.coord([api.value(1), index]);
        var end = api.coord([api.value(2), index]);
        var barWidth = end[0] - start[0];
        var barHeight = api.size([0, 1])[1] * HEIGHT_RATIO;
        var x = start[0];
        var y = (start[1] - barHeight) - (barHeight / 3);
        var dependencies = JSON.parse(api.value(3).replaceAll('^', '"')).d;
        let links = []
        for(let j = 0; j < dependencies.length; j++){
            var parent = dependencies[j];
            var indexParent = parent[0];
            var startParent = api.coord([parent[1], indexParent]);
            var endParent = api.coord([parent[2], indexParent]);
            var barWidthParent = endParent[0] - startParent[0];
            var barHeightParent = api.size([0, 1])[1] * HEIGHT_RATIO;
            var xParent = startParent[0];
            var yParent = (startParent[1] - barHeightParent) - (barHeightParent / 3);
            let arrow = {}
            if(x < (xParent + barWidthParent / 2)) {
                if(y > yParent) {
                    arrow = {
                        type: 'polygon',
                        ignore: ignore(xParent + barWidthParent / 2, y),
                        shape: {
                            points: [[xParent + barWidthParent / 2 - 5, y - 10],
                                    [xParent + barWidthParent / 2 + 5, y - 10],
                                    [xParent + barWidthParent / 2, y]]
                        },
                        style: api.style({
                            fill: "#000",
                        })
                    }
                } else {
                    arrow = {
                        type: 'polygon',
                        ignore: ignore(xParent + barWidthParent / 2, y + barHeightParent),
                        shape: {
                            points: [[xParent + barWidthParent / 2 - 5, (y + barHeightParent + 10)],
                                    [xParent + barWidthParent / 2 + 5, (y + barHeightParent + 10)],
                                    [xParent + barWidthParent / 2, (y + barHeightParent)]]
                        },
                        style: api.style({
                            fill: "#000",
                        })
                    }
                }
            } else {
                arrow = {
                    type: 'polygon',
                    ignore: ignore(x, y + barHeight / 2),
                    shape: {
                        points: [[x - 5, (y + barHeight / 2) - 5],
                                [x - 5, (y + barHeight / 2) + 5],
                                [x + 5, (y + barHeight / 2)]]
                    },
                    style: api.style({
                        fill: "#000",
                    })
                }
            };
            let circleBottom = {
                type: 'ring',
                ignore: ignore(xParent + barWidthParent / 2, yParent + barHeightParent),
                shape: {
                    cx: xParent + barWidthParent / 2,
                    cy: yParent + barHeightParent,
                    r: 5,
                    r0: 3
                },
                style: api.style({
                    fill: "#000",
                    stroke: "#000"
                })
            };
            let circleTop = {
                type: 'ring',
                ignore: ignore(xParent + barWidthParent / 2, yParent),
                shape: {
                    cx: xParent + barWidthParent / 2,
                    cy: yParent,
                    r: 5,
                    r0: 3
                },
                style: api.style({
                    fill: "#000",
                    stroke: "#000"
                })
            };
            let verticalLine = {
                type: 'line',
                shape: {
                    x1: xParent + barWidthParent / 2,
                    y1: yParent + barHeightParent,
                    x2: xParent + barWidthParent / 2,
                    y2: y + barHeightParent / 2
                },
                style: api.style({
                    fill: "#000",
                    stroke: "#000"
                })
            };
            let horizontalLine = {
                type: 'line',
                shape: {
                    x1: xParent + barWidthParent / 2,
                    y1: y + barHeightParent / 2,
                    x2: x,
                    y2: y + barHeightParent / 2
                },
                style: api.style({
                    fill: "#000",
                    stroke: "#000"
                })
            };
            links.push({
                type: 'group',
                children: [circleBottom, circleTop, verticalLine, horizontalLine, arrow]
            });
        }
        return {
            type: 'group',
            children: links
        };
    }

    // Render horizontal bands. Data Point: [Y-value, start, end, color]
    _renderHBand(params, api) {
        var index = api.value(0);
        var color = api.value(3);
        var start = api.coord([api.value(1), index]);
        var end = api.coord([api.value(2), index]);
        var barWidth = end[0] - start[0];
        var barHeight = api.size([0, 1])[1];
        var x = start[0];
        var y = start[1] - barHeight;
        var rectNormal = {
            x: x,
            y: y,
            width: barWidth,
            height: barHeight
        };
        var rectSystem = {
            x: params.coordSys.x,
            y: params.coordSys.y,
            width: params.coordSys.width,
            height: params.coordSys.height
        };
        rectNormal = echarts.graphic.clipRectByRect(rectNormal, rectSystem);
        return {
            type: 'group',
            silent: true,
            children: [{
                type: 'rect',
                ignore: !rectNormal,
                shape: rectNormal,
                style: api.style({
                    fill: color
                })
            }]
        };
    }

    // Render a vertical line at a given value. Single Data Point: [line at, text label, color]
    _renderVLine(params, api) {
        var lineAt = api.coord([api.value(0), 0]);
        var x = lineAt[0];
        var y = params.coordSys.y;
        var y_end = y + params.coordSys.height;
        let color = api.value(2);
        let text = api.value(1);
        var textWidth = echarts.format.getTextRect(text).width;
        var elements = [];
        if(textWidth > 0) {
            elements.push({
                type: 'text',
                style: {
                    x: x - textWidth / 2,
                    y: y,
                    text: text,
                    textVerticalAlign: 'bottom',
                    textAlign: 'left',
                    textFill: color
                }
            });
        }
        elements.push({
            type: 'line',
            shape: {
                x1: x,
                y1: y,
                x2: x,
                y2: y_end
            },
            style: api.style({
                fill: color,
                stroke: color
            })
        });
        return {
            type: 'group',
            silent: true,
            children: elements
        };
    }
}

customElements.define('so-chart', SOChart);
