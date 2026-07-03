import {createRouter, createWebHistory} from "vue-router";
import {isUnauthorized} from "@/net/index.js";
import {ElMessage} from "element-plus";

const router = createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path:'',
                    name:'welcome-login',
                    component:()=>import('@/views/welcome/LoginPage.vue')
                }, {
                    path:'register',
                    name:'welcome-register',
                    component:()=>import('@/views/welcome/RegisterPage.vue')
                },{
                    path:'reset',
                    name:'welcome-reset',
                    component:()=>import('@/views/welcome/ResetPage.vue')
                }
            ]
        },{
            path: '/index',
            name: 'index',
            component:()=>import('@/views/IndexView.vue'),
            children: [
                {
                    path:'',
                    name:'topics',
                    component:()=>import('@/views/forum/Forum.vue'),
                    children:[
                        {
                            path:'',
                            name:'topic-list',
                            component:()=>import('@/views/forum/TopicList.vue'),
                        },
                        {
                            path:'topic-detail/:tid',
                            name:'topic-detail',
                            component:()=>import('@/views/forum/TopicDetail.vue'),
                        }
                    ]
                },
                {
                    path:'user-setting',
                    name:'user-setting',
                    component:()=>import('@/views/settings/UserSetting.vue'),
                },
                {
                    path:'user-privacy',
                    name:'user-privacy',
                    component:()=>import('@/views/settings/PrivacySetting.vue'),
                }
            ]
        },{
            path: '/admin',
            name: 'admin',
            component:()=>import('@/views/AdminView.vue'),
            children: [
                ]
        }
    ]
});

router.beforeEach((to, from, next) => {
    const unauthorized = isUnauthorized()  // true = 未登录

    // 安全获取路由名称（避免 undefined 报错）
    const routeName = to.name || ''

    // 1. 已登录用户不允许访问 welcome- 系列页面
    if (!unauthorized && routeName.startsWith('welcome-')) {
        next('/index')
    }
    // 2. 未登录用户只允许访问 welcome- 系列页面
    else if (unauthorized && !routeName.startsWith('welcome-')) {
        ElMessage.warning('请您先登录再进行访问')
        next('/')  // 重定向到登录页
    }
    // 3. 其余情况正常放行
    else {
        next()
    }
})

export default router