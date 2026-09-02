import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css'
import { createPinia } from "pinia";
import '@/assets/quill.css'

axios.defaults.baseURL=''

const app = createApp(App)


app.use(createPinia())
app.use(router)


app.mount('#app')
