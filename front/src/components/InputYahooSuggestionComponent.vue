<template>
    <v-text-field hide-details color="primary" v-model="internalTicker" label="Symbol" required
        @input="handleInputChange" @click="handleInputChange">
    </v-text-field>
    <ul v-if="suggestions.length > 0 || suggestionsLoading" class="suggestions-list"
        :id="'suggestions-list-' + internalTicker">
        <li v-for="suggestion in suggestions" :key="suggestion.symbol" @click="selectSuggestion(suggestion)">
            {{ suggestion.symbol }} - {{ suggestion.shortName }}
        </li>
        <li v-if="suggestionsLoading && suggestions.length > 0">Loading ...</li>
    </ul>
</template>

<script>
import axios from 'axios';
import { ref, watchEffect } from 'vue'

let source = null;

export default {
    props: ['emitSelectedSuggestion', 'emitResetError', 'initialValue'],
    setup(props) {
        const internalTicker = ref(props.initialValue);
        const suggestions = ref([])
        const suggestionsLoading = ref(false);

        const handleInputChange = async () => {
            try {
                if (source) {
                    source.cancel();
                }
                source = axios.CancelToken.source();
                const cancelToken = source.token;

                props.emitResetError();
                if (internalTicker.value.trim().length < 1) {
                    suggestions.value = [];
                    return;
                }
                props.emitSelectedSuggestion(internalTicker.value);
                suggestions.value = [];
                suggestionsLoading.value = true;
                const response = await axios.get(`/yahoo/v1/finance/search?q=${encodeURIComponent(internalTicker.value.trim())}`, {
                    cancelToken
                })
                if (!cancelToken.reason) {
                    suggestionsLoading.value = false;
                    if (internalTicker.value.trim().length < 1) {
                        suggestions.value = [];
                        return;
                    }
                    if ("quotes" in response.data && response.data.quotes === 0) {
                        console.error('End of data for ' + this.ticker.value.trim());
                        return;
                    }
                    suggestions.value = response.data.quotes.map(one => ({
                        symbol: one.symbol,
                        shortName: one.longname
                    }));
                }
            } catch (error) {
                if (axios.isCancel(error)) {
                    //ignore
                } else {
                    console.error('Error fetching suggestions:', error);
                    suggestions.value = [];
                }
            }
        }

        const selectSuggestion = (suggestion) => {
            internalTicker.value = suggestion.symbol;
            suggestions.value = [];
            props.emitSelectedSuggestion(suggestion);
        }

        const handleClick = (event) => {
            if (!event.target.closest('#suggestions-list')) {
                suggestions.value = [];
                suggestionsLoading.value = false;
            }
        }

        watchEffect(() => {
            document.addEventListener('click', handleClick);
            return () => {
                document.removeEventListener('click', handleClick);
            };
        });

        return {
            suggestions,
            handleInputChange,
            selectSuggestion,
            suggestionsLoading,
            internalTicker
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
