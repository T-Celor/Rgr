<template>
    <v-container>
        <v-card>
            <v-card-text>
                <v-row align="center" justify="center">
                    <h2>Welcome to Rgr !</h2>
                    <br><br>
                    <v-col cols="12" class="text-center">
                        <v-btn @click="redirectTo('/wallet')" color="primary">Wallet simulation</v-btn>
                    </v-col>
                    <br>
                    <v-col cols="12" class="text-center">
                        <v-btn @click="redirectTo('/asset')" color="primary">Asset view</v-btn>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
    </v-container>
</template>

<script>
export default {
    name: 'HomeView',
    methods: {
        redirectTo(route) {
            this.$router.push(route);
        }
    }
}
</script>