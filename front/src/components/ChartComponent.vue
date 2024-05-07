<template>
    <v-container>
        <v-row align="center" justify="center">
            <v-col cols="12" class="text-center">
                <v-card>
                    <v-card-title><b>{{ this.title }}</b> - Metrics</v-card-title>
                    <v-card-text>
                        <v-data-table :headers="headers" :items="formattedData">
                        </v-data-table>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
        <br>
        <v-row align="center" justify="center">
            <v-col cols="12" class="text-center">
                <v-card>
                    <v-card-title><b>{{ this.title }}</b> - Chart</v-card-title>
                    <v-card-text>
                        <canvas ref="chart" id="graph"></canvas>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import Chart from 'chart.js/auto';
import { SimpleLinearRegression } from 'ml-regression-simple-linear';

export default {
    props: ['data', 'title', 'asset'],
    data() {
        return {
            Acagr: 0,
            Rcagr: 0,
            confidence: 0
        };
    },
    mounted() {
        this.renderChart();
    },
    computed: {
        formattedData() {
            return [
                { name: 'Start value', value: `${this.data.startValue.toFixed(2)}` },
                { name: 'End value', value: `${this.data.endValue.toFixed(2)}` },
                { name: 'CAGR', value: `${this.data.cagr.toFixed(2)}%` },
                { name: 'Max drawdown', value: `${this.data.drawdown.toFixed(2)}%` },
                { name: 'Recovery max drawdown', value: `${this.data.recoveryRatio.toFixed(2)} (${(this.data.recoveryRatio / 12).toFixed(2)} years)` },
                { name: 'Volatility', value: `${this.data.volatility.toFixed(2)}%` },
                { name: 'Sharpe Ratio', value: `${this.data.sharpeRatio.toFixed(2)}` },
                { name: 'PE Ratio', value: `${this.data.pe.toFixed(2)}` },
                { name: 'Dividend', value: `${this.data.startValue.toFixed(2)}%` },
                { name: 'ROE', value: `${this.data.roe.toFixed(2)}%` },
                { name: '# Month', value: `${this.data.value.length} (${(this.data.value.length / 12).toFixed(2)} years)` },
                { name: 'CAGR', value: `${this.Acagr.toFixed(2)}%` },
                { name: 'Channel CAGR', value: `${this.Rcagr.toFixed(2)}%` },
                { name: 'Channel Confidence', value: `${this.confidence.toFixed(2)}%` }
            ];
        }
    },
    methods: {
        renderChart() {
            if (this.data) {
                const valueLogarithmed = this.data.value.map(value => Math.log(value));
                const dateX = this.data.timestamp.map(timestamp => new Date(timestamp * 1000).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }));
                const ctx = this.$refs.chart.getContext('2d');
                const chartData = {
                    labels: dateX,
                    datasets: [{
                        label: this.asset,
                        data: valueLogarithmed.map(value => Math.exp(value)),
                        borderColor: 'blue',
                        fill: false
                    }]
                };

                const regression = new SimpleLinearRegression(valueLogarithmed.map((point, index) => index), valueLogarithmed);
                var regressionLine = valueLogarithmed.map((point, index) => regression.predict(index));
                const residuals = valueLogarithmed.map((value, index) => value - regressionLine[index]);
                var stdDev = Math.sqrt(residuals.reduce((sum, value) => sum + Math.pow(value, 2), 0) / residuals.length);

                const oneStdDevUpper = regressionLine.map(value => value + stdDev);
                const oneStdDevLower = regressionLine.map(value => value - stdDev);
                const twoStdDevUpper = regressionLine.map(value => value + 2 * stdDev);
                const twoStdDevLower = regressionLine.map(value => value - 2 * stdDev);

                regressionLine = regressionLine.map(value => Math.exp(value))

                chartData.datasets.push(
                    {
                        label: 'Linear Regression',
                        data: regressionLine,
                        borderColor: 'red',
                        fill: false,
                        borderDash: [2, 2],
                        pointRadius: 0,
                        showLine: true
                    },
                    {
                        label: 'Channel (+1 StdDev)',
                        data: oneStdDevUpper.map(value => Math.exp(value)),
                        borderColor: '#e1e1e1',
                        fill: false,
                        borderDash: [2, 2],
                        pointRadius: 0,
                        showLine: true
                    },
                    {
                        label: 'Channel (-1 StdDev)',
                        data: oneStdDevLower.map(value => Math.exp(value)),
                        borderColor: '#e1e1e1',
                        fill: false,
                        borderDash: [2, 2],
                        pointRadius: 0,
                        showLine: true
                    },
                    {
                        label: 'Channel (+2 StdDev)',
                        data: twoStdDevUpper.map(value => Math.exp(value)),
                        borderColor: '#e1e1e1',
                        fill: false,
                        borderDash: [2, 2],
                        pointRadius: 0,
                        showLine: true
                    },
                    {
                        label: 'Channel (-2 StdDev)',
                        data: twoStdDevLower.map(value => Math.exp(value)),
                        borderColor: '#e1e1e1',
                        fill: false,
                        borderDash: [2, 2],
                        pointRadius: 0,
                        showLine: true
                    }
                );

                const n = this.data.value.length / 12;
                this.Acagr = (Math.pow(this.data.value[this.data.value.length - 1] / this.data.value[0], 1 / n) - 1) * 100;
                this.Rcagr = (Math.pow(regressionLine[regressionLine.length - 1] / regressionLine[0], 1 / n) - 1) * 100;
                const { r2 } = regression.score(valueLogarithmed.map((point, index) => index), valueLogarithmed);
                this.confidence = r2 * 100;

                new Chart(ctx, {
                    animationEnabled: true,
                    zoomEnabled: true,
                    type: 'line',
                    data: chartData,
                    options: {
                        scales: {
                            y: {
                                type: 'logarithmic',
                                grid: {
                                    display: false
                                }
                            },
                            x: {
                                grid: {
                                    display: false
                                }
                            }
                        },
                        elements: {
                            point: {
                                radius: 0
                            }
                        },
                        responsive: true,
                        maintainAspectRatio: false,
                        tooltips: {
                            mode: 'index',
                            intersect: false,
                        },
                        hover: {
                            mode: 'index',
                            intersect: false,
                        }
                    }
                });
            }
        }
    }
};
</script>

<style>
#graph {
    width: 100%;
    height: 100%;
    min-height: 400px;
    min-width: 100px;
}
</style>
