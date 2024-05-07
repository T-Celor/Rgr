<template>
    <v-container>
        <h2>Wallet module</h2>
        <v-card>
            <v-card-text>
                <v-form @submit.prevent="handleSubmit">
                    <v-container v-for="(ticker, index) in tickers" :key="index" :id="'inputContainer_' + index">
                        <v-row align="center">
                            <v-col cols="10">
                                <InputYahooSuggestionComponent :initialValue="ticker.value"
                                    :emitSelectedSuggestion="(result) => handleSelectedSuggestion(index, result)"
                                    :emitResetError="resetError"></InputYahooSuggestionComponent>
                            </v-col>
                            <v-col cols="auto">
                                <v-btn icon @click="removeInputField(index)" size="x-small">
                                    <v-icon color="error">mdi-minus</v-icon>
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-container>
                    <v-container>
                        <v-row align="center" justify="center">
                            <v-col cols="auto">
                                <v-btn icon @click="addInputFields" color="primary" size="x-small">
                                    <v-icon>mdi-plus</v-icon>
                                </v-btn>
                            </v-col>
                        </v-row>
                        <v-row align="center">
                            <v-col cols="4">
                                <v-text-field color="primary" hide-details v-model.number="balance" label="Wallet value"
                                    required></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row align="center">
                            <v-col cols="auto">
                                <v-checkbox color="primary" v-model="accumulating"
                                    label="include dividend"></v-checkbox>
                            </v-col>
                        </v-row>
                        <v-row align="center" justify="center">
                            <v-col cols="auto">
                                <v-btn :color="error ? 'error' : 'primary'" type="submit"
                                    :disabled="isButtonDisabled || loading || error">
                                    <span v-if="loading">Loading...</span>
                                    <span v-else-if="error"><v-icon color="white">mdi-alert-circle-outline</v-icon>
                                        Error
                                        fetching
                                        data</span>
                                    <span v-else>Query</span>
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-form>
            </v-card-text>
        </v-card>
        <br><br>
        <v-card v-if="tickerInfo">
            <ChartComponent :data="tickerInfo" :title="'Wallet'" :asset="'Wallet'" />
        </v-card>
    </v-container>
</template>

<script>
import { ref, computed } from 'vue';
import { getWalletData } from '../services/financeService';
import ChartComponent from '@/components/ChartComponent.vue';
import InputYahooSuggestionComponent from '@/components/InputYahooSuggestionComponent.vue'
import { useRoute } from 'vue-router'

export default {
    components: {
        ChartComponent,
        InputYahooSuggestionComponent
    },
    setup() {
        const route = useRoute()

        const initialInputCount = 3;
        const tickers = ref(Array.from({ length: initialInputCount }, () => ({ value: '' })));
        const balance = ref(null);
        const tickerInfo = ref(null);
        const loading = ref(false);
        const error = ref(false);
        const accumulating = ref(false);

        const handleSubmit = async () => {
            console.log(tickers);
            tickerInfo.value = null;
            loading.value = true;
            error.value = false;

            const tickersList = tickers.value.filter(ticker => ticker.value && ticker.value.trim() !== '').map(ticker => ticker.value).join(',');
            try {
                tickerInfo.value = await getWalletData(tickersList, parseFloat(balance.value), accumulating.value);
                //Update local url
                const queryParams = new URLSearchParams(window.location.search)
                queryParams.set('tickers', tickersList)
                queryParams.set('accumulating', accumulating.value)
                queryParams.set('balance', balance.value)
                const newUrl = `${window.location.pathname}?${queryParams.toString()}`
                window.history.pushState({ path: newUrl }, '', newUrl)
            } catch (err) {
                console.error('Error fetching data:', err);
                error.value = true;
            } finally {
                loading.value = false;
            }
        };

        const isButtonDisabled = computed(() => {
            return tickers.value.every(ticker => ticker.value != undefined && !ticker.value.trim()) || isNaN(parseFloat(balance.value));
        });

        const addInputFields = () => {
            const newTickers = Array.from({ length: initialInputCount }, () => ({ value: '' }));
            tickers.value = tickers.value.concat(newTickers);
        };

        const removeInputField = (index) => {
            const containerId = 'inputContainer_' + index;
            const container = document.getElementById(containerId);
            if (container) {
                container.remove();
            }
            tickers.value[index].value = "";
        };

        const handleSelectedSuggestion = (index, result) => {
            tickers.value[index].value = result.symbol;
        }

        const resetError = () => {
            error.value = false;
        }

        //Init
        const params = route.query
        if (params.tickers && params.accumulating && params.balance) {
            tickers.value = params.tickers.split(",").map(ticker => ({
                value: ticker
            }));
            accumulating.value = params.accumulating === 'true';
            balance.value = params.balance;
            handleSubmit();
        }

        return {
            tickers,
            balance,
            tickerInfo,
            accumulating,
            isButtonDisabled,
            error,
            loading,
            removeInputField,
            handleSubmit,
            addInputFields,
            handleSelectedSuggestion,
            resetError
        };
    }
};
</script>
