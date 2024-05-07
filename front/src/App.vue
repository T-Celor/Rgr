<template>
  <v-app>
    <NavbarComponent />
    <v-main fluid style=" overflow-y: auto; overflow-y: auto;">
      <v-container>
        <RouterView />
      </v-container>
      <FooterComponent />
    </v-main>
  </v-app>
</template>

<script>
import NavbarComponent from "@/components/NavbarComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";

export default {
  name: "App",
  components: {
    NavbarComponent,
    FooterComponent
  }
};
</script>

<style></style>
