import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css'
import { createPinia } from "pinia";
import '@/assets/quill.css'

const apiBaseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
axios.defaults.baseURL = apiBaseURL

const app = createApp(App)


app.use(createPinia())
app.use(router)


app.mount('#app')
