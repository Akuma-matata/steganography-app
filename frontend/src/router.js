import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from './views/Home.vue';
import Encode from './views/Encode.vue';
import Decode from './views/Decode.vue';
import History from './views/History.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/encode',
    name: 'Encode',
    component: Encode
  },
  {
    path: '/decode',
    name: 'Decode',
    component: Decode
  },
  {
    path: '/history',
    name: 'History',
    component: History
  },
  {
    path: '*',
    redirect: '/'
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

export default router;