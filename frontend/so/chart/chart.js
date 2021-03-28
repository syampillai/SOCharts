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
    }
}

customElements.define('so-chart', SOChart);
