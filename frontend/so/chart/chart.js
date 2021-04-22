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
        this.data = null;
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

    updateChart(options) {
        var json = JSON.parse(options);
        if(!this.chart || this.chart == null) {
            this.chart = echarts.init(this.shadowRoot.getElementById(this.idChart));
        }
        this.chart.setOption(json);
        this.data = json.dataset.source;
    }

    pushData(data) {
        this._pushData(data, 1);
    }

    appendData(data) {
        this._pushData(data, 0);
    }

    resetData(data) {
        this._pushData(data, 2);
    }

    _pushData(data, code) {
        if(this.chart == null) {
            return;
        }
        var no = { dataset: { source: { } } };
        var valueSets = JSON.parse(data)["d"];
        var o = this.chart.getOption();
        var d;
        for(const valueSet of valueSets) {
            d = o.dataset[0].source["d" + valueSet.i];
            if(code == 2) { // Reset
                d = [];
            } else {
                if(code == 1) { // Push, not append
                    d.shift();
                }
                d.push(valueSet.v);
            }
            no.dataset.source["d" + valueSet.i] = d;
        }
        this.chart.setOption(no);
    }
}

customElements.define('so-chart', SOChart);
