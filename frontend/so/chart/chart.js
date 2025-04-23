import { LitElement, html } from 'lit';
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
        this.clickHandler1 = null;
        this.clickHandler2 = null;
        this.data = {};
        this.allOptions = "";
        this.minw = "5vw";
        this.minh = "5vw";
        this.maxw = "6000px";
        this.maxh = "6000px";
        this.width = "33vw";
        this.height = "33vh";

        // Keep a reference to pending resize event. We use this to not trigger too many render events on continuous resize
        this.pendingResizeEvent = null;
        this.resizeListener = () => {
            if(this.chart) {
                // Clear previous pending event (if any) and register a new one. This way only the last one will trigger
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
        if(this.chart && !changedProps.has("idChart")) {
            this.chart.resize();
        }
    }

    // noinspection JSUnusedGlobalSymbols
    clearChart() {
        if(this.chart == null) {
            return;
        }
        this.chart.enableClickEvents(false);
        this.chart.clear();
    }

    destroyChart() {
        if(this.chart == null) {
            return;
        }
        this.chart.dispose();
        this.chart = null;
    }

    // noinspection JSUnusedGlobalSymbols
    setThemeAndLocale(theme, locale, renderer) {
        this.destroyChart();
        this.updateChart(false, this.allOptions, theme, locale, renderer, this.clickHandler1 != null);
    }

    updateChart(full, options, theme, locale, renderer, enableClicks) {
        if(full) {
            this.allOptions = options;
        }
        const json = JSON.parse(options);
        this._stuff(json);
        if(!this.chart) {
            this.chart = echarts.init(this.shadowRoot.getElementById(this.idChart), theme, {
                locale: locale,
                renderer: renderer
            });
        }
        this.chart.setOption(json);
        this.enableClickEvents(enableClicks);
    }

    clearData() {
        this.data = {};
    }

    updateData(serial, data) {
        if(typeof serial !== 'undefined') {
            this.initData(serial, data);
        }
        const json = JSON.parse(this.allOptions);
        this._stuff(json);
        this.chart.setOption(json);
    }

    initData(serial, data) {
        this.data["d" + serial] = JSON.parse(data)["d"];
    }

    // noinspection JSUnusedGlobalSymbols
    pushData(data) {
        data = JSON.parse(data)["d"];
        for(let k in data) {
            this.data[k].push(data[k]);
            this.data[k].shift();
        }
        this.updateData();
    }

    // noinspection JSUnusedGlobalSymbols
    appendData(data) {
        data = JSON.parse(data)["d"];
        for(let k in data) {
            this.data[k].push(data[k]);
        }
        this.updateData();
    }

    // noinspection JSUnusedGlobalSymbols
    resetData(serials) {
        serials = JSON.parse(serials)["d"];
        serials.forEach(v => {
            this.data["d" + v] = [];
        });
        this.updateData();
    }

    // noinspection JSUnusedGlobalSymbols
    enableClickEvents(enable) {
        if(!this.chart) {
            return;
        }
        if(enable) {
            if(!this.clickHandler1) {
                this.clickHandler1 = e => {
                    // console.log(e);
                    this.$server.onClick(e.componentType, e.componentIndex, e.componentSubType, e.seriesId, e.targetType, JSON.stringify(e.value));
                };
                this.chart.on('click', this.clickHandler1);
                this.clickHandler2 = e => {
                    if(!e.target) {
                        this.$server.onClick("", 0, "", "", "", "");
                    }
                };
                this.chart.getZr().on('click', this.clickHandler2);
            }
        } else {
            if(this.clickHandler2) {
                this.chart.off('click', this.clickHandler1);
                this.clickHandler1 = null;
                this.chart.getZr().off('click', this.clickHandler2);
                this.clickHandler2 = null;
            }
        }
    }

    _stuff(obj) {
        this._stuffData(obj);
        this._stuffDataSet(obj);
        this._stuffFunc(obj);
        this._stuffFuncP(obj);
        this._stuffRenderer(obj);
    }

    _stuffFunc(obj) {
        let o;
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
        let o;
        for(let k in obj) {
            o = obj[k];
            if(typeof o === 'object') {
                // noinspection JSUnresolvedReference
                if(typeof o.functionP === 'object') {
                    // noinspection JSUnresolvedReference
                    obj[k] = p => this._formatter(p, o.functionP.body);
                } else {
                    this._stuffFuncP(o);
                }
            }
        }
    }

    _stuffRenderer(obj) {
        let o;
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
        let o;
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
    // Data Point: [Y-value, label, start, end, completed, bar color, prefix color, non-progress color, font-size]
    // noinspection JSUnusedGlobalSymbols
    _renderHBar(params, api) {
        const HEIGHT_RATIO = 0.6;
        const index = api.value(0);
        const label = api.value(1);
        const start = api.coord([api.value(2), index]);
        const end = api.coord([api.value(3), index]);
        const donePercentage = api.value(4);
        const color = api.value(5);
        const prefixColor = api.value(6);
        const nonProgressColor = api.value(7);
        const fontSize = api.value(8);
        let barWidth = end[0] - start[0];
        let barHeight = api.size([0, 1])[1];
        let shiftY;
        if(color === prefixColor) { // Not a Gantt chart
            shiftY = 0;
        } else {
            shiftY = -(barHeight / 2);
        }
        barHeight *= HEIGHT_RATIO;
        let x = start[0];
        const y = start[1] - (barHeight / 2) + shiftY;
        x += 3;
        barWidth -= 3;
        if(barWidth < 0) {
            barWidth = 0;
        }
        const rectSystem = {
            x: params.coordSys.x,
            y: params.coordSys.y,
            width: params.coordSys.width,
            height: params.coordSys.height
        };
        let rectPrefix = {
            x: x - 3,
            y: y,
            width: 3,
            height: barHeight
        };
        rectPrefix = echarts.graphic.clipRectByRect(rectPrefix, rectSystem);
        let rectBox = {
            x: x,
            y: y,
            width: barWidth,
            height: barHeight
        };
        rectBox = echarts.graphic.clipRectByRect(rectBox, rectSystem);
        let rectPercent;
        if(donePercentage >= 0 && donePercentage < 100) {
            const completedWidth = (barWidth + 3) * donePercentage / 100;
            rectPercent = {
                x: x + completedWidth - 3,
                y: y,
                width: barWidth - completedWidth + 3,
                height: barHeight / 3
            };
            rectPercent = echarts.graphic.clipRectByRect(rectPercent, rectSystem);
        }
        let hideText;
        if(shiftY === 0) { // Not a Gantt chart
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
                    font: 'bold ' + fontSize + 'px sans-serif'
                }
            }]
        };
    }

    // Render labels on vertical axis - labels can be connected within the same group too (used by Gantt chart)
    // Data Point: [Y-value, group label, connected, label, sub-label, bar color, font-size, extra-font-size]
    // noinspection JSUnusedGlobalSymbols
    _renderVAxisLabel(params, api) {
        const minY = params.coordSys.y;
        // const maxY = minY + params.coordSys.height;
        const width = params.coordSys.x - 15;
        const index = api.value(0);
        const groupName = api.value(1);
        const connected = api.value(2);
        const label = api.value(3);
        const sublabel = api.value(4);
        const color = api.value(5);
        const fontSize = api.value(6);
        const extraFontSize = api.value(7);
        const barHeight = api.size([0, 1])[1];
        const y = api.coord([0, index])[1] - barHeight;
        let ry = y + 0.2 * barHeight, ty1 = y + 0.6 * barHeight, ty2 = y + 0.8 * barHeight, rh = 0.8 * barHeight;
        if(ry < minY) {
            let d = minY - ry;
            ry = minY;
            rh -= d;
        }
        let rect = {
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
                    font: 'bold ' + fontSize + 'px/1 sans-serif'
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
                    font: 'bold ' + extraFontSize + 'px/1 sans-serif'
                }
            }]
        };
        if(connected === 1) {
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
    // noinspection JSUnusedGlobalSymbols
    _renderDependency(params, api) {
        const minX = params.coordSys.x;
        const minY = params.coordSys.y;
        const maxX = minX + params.coordSys.width;
        const maxY = minY + params.coordSys.height;
        const ignore = (x, y) => x < minX || x > maxX || y < minY || y > maxY;
        const HEIGHT_RATIO = 0.6;
        const index = api.value(0);
        const start = api.coord([api.value(1), index]);
        // const end = api.coord([api.value(2), index]);
        //var barWidth = end[0] - start[0];
        const barHeight = api.size([0, 1])[1] * HEIGHT_RATIO;
        const x = start[0];
        const y = (start[1] - barHeight) - (barHeight / 3);
        const dependencies = JSON.parse(api.value(3).replaceAll('^', '"')).d;
        let links = []
        for(let j = 0; j < dependencies.length; j++){
            const parent = dependencies[j];
            const indexParent = parent[0];
            const startParent = api.coord([parent[1], indexParent]);
            const endParent = api.coord([parent[2], indexParent]);
            const barWidthParent = endParent[0] - startParent[0];
            const barHeightParent = api.size([0, 1])[1] * HEIGHT_RATIO;
            const xParent = startParent[0];
            const yParent = (startParent[1] - barHeightParent) - (barHeightParent / 3);
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
            }
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
    // noinspection JSUnusedGlobalSymbols
    _renderHBand(params, api) {
        const index = api.value(0);
        const color = api.value(3);
        const start = api.coord([api.value(1), index]);
        const end = api.coord([api.value(2), index]);
        const barWidth = end[0] - start[0];
        const barHeight = api.size([0, 1])[1];
        const x = start[0];
        const y = start[1] - barHeight;
        let rectNormal = {
            x: x,
            y: y,
            width: barWidth,
            height: barHeight
        };
        const rectSystem = {
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
    // noinspection JSUnusedGlobalSymbols
    _renderVLine(params, api) {
        const lineAt = api.coord([api.value(0), 0]);
        const x = lineAt[0];
        const y = params.coordSys.y;
        const y_end = y + params.coordSys.height;
        let color = api.value(2);
        let text = api.value(1);
        const textWidth = echarts.format.getTextRect(text).width;
        const elements = [];
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
