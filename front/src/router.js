import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import AssetView from '@/views/AssetView.vue';
import WalletView from '@/views/WalletView.vue';

const routes = [
  { path: '/', component: HomeView },
  { path: '/asset', component: AssetView },
  { path: '/wallet', component: WalletView }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
