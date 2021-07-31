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
        var o;
        for(let k in obj) {
            if(k === "data") return true;
            o = obj[k];
            if(typeof o === 'object') {
                if(this._containsData(o)) return true;
            }
        }
        return false;
    }
}

customElements.define('so-chart', SOChart);
