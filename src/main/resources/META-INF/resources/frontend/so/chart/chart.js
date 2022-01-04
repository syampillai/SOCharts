import { LitElement, html, css, property, customElement } from 'lit-element'
import * as echarts from 'echarts/dist/echarts.min'

export class SOChart extends LitElement {

    render() {
        return html`<div id="${this.idChart}"
                         style="min-width:${this.minw};max-width:${this.maxw};width:${this.width};min-height:${this.minh};max-height:${this.maxh};height:${this.height};">
        </div>`;
    }

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

    constructor() {
        super();
        this.idChart = null;
        this.data = {};
        this.dataOptions = "";
        this.minw = "5vw";
        this.minh = "5vw";
        this.maxw = "6000px";
        this.maxh = "6000px";
        this.width = "33vw";
        this.height = "33vh";
        this.events = [];
    }

    addEvent(event, data) {
        this.events.push({event, data})
    }
    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }

    disconnectedCallback() {
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

    updateChart(full, options) {
        if(full) {
            this.dataOptions = JSON.parse(options);
            this._trimToData(this.dataOptions);
            this.dataOptions = JSON.stringify(this.dataOptions);
        }
        var json = JSON.parse(options);
        this._stuff(json);
        if(!this.chart || this.chart == null) {
            this.chart = echarts.init(this.shadowRoot.getElementById(this.idChart));
        }
        this.events.forEach(event => {
            this.chart.on(event.event, {name: event.data}, params => {
                console.log(event.event);
                console.log(event.data);
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
        var json = JSON.parse(this.dataOptions);
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

    _trimToData(obj) {
        var removed = true;
        var o;
        while(removed) {
            removed = false;
            for(let k in obj) {
                if(k === "dataset" || k === 'data' || k === 'id') continue;
                o = obj[k];
                if(typeof o === 'object') {
                    if(this._containsData(o)) {
                        this._trimToData(o);
                    } else {
                        delete obj[k];
                        removed = true;
                        break;
                    }
                } else {
                    delete obj[k];
                    removed = true;
                    break;
                }
            }
        }
    }

    _containsData(obj) {
        return this._containsTag(obj, "data");
    }

    _containsTag(obj, tag) {
        var o;
        for(let k in obj) {
            if(k === tag) return true;
            o = obj[k];
            if(typeof o === 'object') {
                if(this._containsData(o)) return true;
            }
        }
        return false;
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
}

customElements.define('so-chart', SOChart);
