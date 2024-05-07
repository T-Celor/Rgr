<template>
    <v-app-bar app color="primary">
        <v-app-bar-nav-icon @click.stop="drawer = true"></v-app-bar-nav-icon>
        <v-btn to="/" variant="text">
            <v-toolbar-title>
                Rgr
            </v-toolbar-title>
        </v-btn>
        <v-spacer></v-spacer>
        <v-container style="width: 50%; max-width: 300px;">
            <v-autocomplete v-model="select" :loading="loading" :items="items"
                @input="querySelections($event.target.value)" @click="querySelections($event.target.value)" flat
                hide-details label="Ticker" solo clearable>
                <template #item="{ item }">
                    <v-list-item class="d-flex">
                        <div>
                            <v-simple-checkbox color="primary" @click="handleItemClick(item)" />
                        </div>
                        <div class="ml-2">{{ item }}</div>
                    </v-list-item>
                </template></v-autocomplete></v-container>
        <v-btn icon>
            <v-icon>mdi-magnify</v-icon>
        </v-btn>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" absolute right temporary>
        <v-list>
            <v-list-item to="/wallet" @click="drawer = false">
                <v-list-item-title>Wallet</v-list-item-title>
            </v-list-item>
            <v-list-item to="/asset" @click="drawer = false">
                <v-list-item-title>Asset</v-list-item-title>
            </v-list-item>
        </v-list>
    </v-navigation-drawer>
</template>

<script>
import axios from 'axios';

let source = null;

export default {
    data() {
        return {
            drawer: false,
            loading: false,
            items: [],
            search: null,
            select: null,
        };
    },
    methods: {
        querySelections(value) {
            if (!value || value.trim().length < 1) {
                this.items = [];
                return;
            }

            if (source) {
                source.cancel();
            }
            source = axios.CancelToken.source();
            const cancelToken = source.token;

            this.loading = true;
            this.select = value;
            try {
                axios.get(`/yahoo/v1/finance/search?q=${encodeURIComponent(value.trim())}`, {
                    cancelToken
                })
                    .then(response => {
                        this.items = response.data.quotes.map(one => (one.quoteType + " - " + one.symbol + " - " + one.shortname + " - " + one.exchDisp));
                    })
                    .catch(error => {
                        if (axios.isCancel(error)) {
                            //ignore
                        } else {
                            console.error('Error fetching suggestions:', error);
                        }
                    })
                    .finally(() => {
                        this.loading = false;
                    });
            } catch (error) {
                if (axios.isCancel(error)) {
                    //ignore
                } else {
                    console.error('Error fetching suggestions:', error);
                    this.loading = false;
                }
            }
        },
        handleItemClick(event) {
            console.log(event.split(" - ")[1])
            if (event) {
                //const ticker = selectedItem.ticker; // Assuming selectedItem is an object with a 'ticker' property
                //const accumulating = true; // Assuming accumulating is always true

                // Navigate to the constructed URL
                //window.location.href = url;
            }
        }
    },
};
</script>

<style scoped></style>
