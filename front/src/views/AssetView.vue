<template>
  <v-container>
    <h2>Single asset module</h2>
    <v-card>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-container>
            <v-row align="center" justify="center">
              <v-col cols="12">
                <InputYahooSuggestionComponent :initialValue="ticker" :emitSelectedSuggestion="handleSelectedSuggestion"
                  :emitResetError="resetError"></InputYahooSuggestionComponent>
              </v-col>
            </v-row>
            <v-row align="center" justify="center">
              <v-col cols="12">
                <v-checkbox color="primary" v-model="accumulating" label="include dividend"></v-checkbox>
              </v-col>
            </v-row>
            <v-row align="center" justify="center">
              <v-col cols="auto">
                <v-btn :color="error ? 'error' : 'primary'" type=" submit"
                  :disabled="isButtonDisabled || loading || error">
                  <span v-if="loading">Loading...</span>
                  <span v-else-if="error"><v-icon color="white">mdi-alert-circle-outline</v-icon> Error fetching
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
    <div v-if="tickerInfo">
      <ChartComponent :data="tickerInfo" :title="`${tickerLongName}`" :asset="`${ticker}`" />
    </div>
  </v-container>
</template>

<script>
import { ref, computed } from 'vue'
import { getFinanceData } from '../services/financeService'
import ChartComponent from '@/components/ChartComponent.vue'
import InputYahooSuggestionComponent from '@/components/InputYahooSuggestionComponent.vue'
import { useRoute } from 'vue-router'


export default {
  components: {
    ChartComponent,
    InputYahooSuggestionComponent
  },
  setup() {
    const route = useRoute()

    var ticker = ref("")
    var tickerLongName = ref('')
    var tickerInfo = ref(null)
    var loading = ref(false)
    var error = ref(false)
    var accumulating = ref(false)

    const handleSubmit = async () => {
      tickerInfo.value = null;
      loading.value = true;
      error.value = false;
      try {
        const chartTicker = await getFinanceData(ticker.value, accumulating.value);
        if (chartTicker.value.length < 3) {
          throw new Error("Ticker info value must have at least 3 month data.");
        }
        tickerInfo.value = chartTicker;
        tickerLongName.value = chartTicker.longName;
        //Update local url
        const queryParams = new URLSearchParams(window.location.search)
        queryParams.set('ticker', ticker.value)
        queryParams.set('accumulating', accumulating.value)
        const newUrl = `${window.location.pathname}?${queryParams.toString()}`
        window.history.pushState({ path: newUrl }, '', newUrl)
      } catch (err) {
        console.error('Error fetching data:', err);
        error.value = true;
      } finally {
        loading.value = false;
      }
    }

    const isButtonDisabled = computed(() => {
      return ticker.value !== undefined && !ticker.value.length > 0
    })

    const handleSelectedSuggestion = (result) => {
      ticker.value = result.symbol;
    }

    function resetError() {
      error.value = false;
    }

    //Init
    const params = route.query
    if (params.ticker && params.accumulating) {
      accumulating.value = params.accumulating === 'true'
      handleSelectedSuggestion(params.ticker)
      handleSubmit()
    }

    return {
      ticker,
      tickerLongName,
      tickerInfo,
      accumulating,
      handleSubmit,
      isButtonDisabled,
      error,
      loading,
      handleSelectedSuggestion,
      resetError
    }
  }
}
</script>

<style>
.suggestions-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
}

.suggestions-list li {
  padding: 10px;
  cursor: pointer;
}

.suggestions-list li:hover {
  background-color: #f1f1f1;
}
</style>
