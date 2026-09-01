<script setup>

import {
    Bell,
    ChatDotSquare,
    Collection, Comment, DataLine,
    Document, Files, Location,
    Lock, Message, Monitor,
    Notification,
    Operation,
    Position, School, Umbrella,
    User
} from "@element-plus/icons-vue";
import UserInfo from "@/components/UserInfo.vue";
import {get} from "@/net";
import {useStore} from "@/store";
import {inject, onMounted, ref} from "vue";
import router from "@/router";
import {useRoute, useRouter} from "vue-router";

const adminMenu = [
  {
    title:'校园论坛管理',
    icon:Location,
    sub:[
      {title:'用户管理', icon:User,index:'/admin/user'},
      {title:'邮件管理', icon:Message,index:'/admin/email'},
      {title:'帖子广场管理', icon:ChatDotSquare,index:'/admin/forum'},
    ]
  }
]

const route = useRoute();
const loading = inject('userLoading')
const pageTabs = ref([])

function handleTabClick({ props }){
  router.push(props.name)
}

function handleTabClose(name){
  const index = pageTabs.value.findIndex(tab => tab.name === name);
  const isCurrent = name === route.fullPath
  pageTabs.value.splice(index, 1);
  if(pageTabs.value.length > 0){
    if(isCurrent){
      router.push(pageTabs.value[Math.max(0,index - 1)].name);
    }
  }else{
    router.push('/admin')
  }
}

function addAdminTab(menu) {
  if(!menu.index) return
  if(pageTabs.value.findIndex(tab => tab.name === menu.index) < 0){
    pageTabs.value.push({
      title: menu.title,
      name: menu.index
    })
  }
}

onMounted(() =>{
  const initPage = adminMenu
      .flatMap(menu => menu.sub)
      .find(sub => sub.index === route.fullPath)
  if(initPage){
    addAdminTab(initPage)
  }
})

</script>

<template>
  <div class="admin-content" v-loading="loading" element-loading-text="正在进入,请稍后...">
    <el-container style="height: 100%">
      <el-aside width="230px" class="admin-content-aside">
        <div class="logo-box">
          <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"/>
        </div>
        <el-scrollbar style="height: calc(100vh - 57px)">
          <el-menu
              router
              :default-active="$route.path"
              :default-openeds="['1','2']"
              style="min-height: calc(100vh - 57px);border: none"
          >
            <el-sub-menu :index="(index + 1).toString()"
                         v-for="(menu, index) in adminMenu">
              <template #title>
                <el-icon>
                  <component :is="menu.icon"/>
                </el-icon>
                <span><b>{{ menu.title }}</b></span>
              </template>
              <el-menu-item :index="subMenu.index"
                            @click="addAdminTab(subMenu)"
                            v-for="subMenu in menu.sub">
                <template #title>
                  <el-icon>
                    <component :is="subMenu.icon"/>
                  </el-icon>
                  {{ subMenu.title }}
                </template>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-header class="admin-content-header">
          <div style="flex: 1">
            <el-tabs type="card"
                     :model-value="route.fullPath"
                     closable
                     @tab-remove="handleTabClose"
                     @tab-click="handleTabClick"
            >
              <el-tab-pane v-for="tab in pageTabs"
                           :label="tab.title"
                           :name="tab.name"
                           :key="tab.name"/>
            </el-tabs>
          </div>
          <user-info/>
        </el-header>
        <el-main>
          <router-view v-slot="{ Component }">
            <keep-alive>
              <component :is="Component"/>
            </keep-alive>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.admin-content{
  height: 100vh;
  width: 100vw;

  .admin-content-aside{
    border-right: solid 1px var(--el-border-color);

    .logo-box{
      text-align: center;
      padding: 15px 0 10px;
      height: 32px;
      .logo{
        height: 32px;
      }
    }
  }

  .admin-content-header {
    border-bottom: solid 1px var(--el-border-color);
    height: 55px;
    display: flex;
    align-items: center;
    box-sizing: border-box;

    :deep(.el-tabs__header) {
      height: 32px;
      margin-bottom: 0;
      border-bottom: none;
      overflow: visible; /* 防止顶部边框被裁 */
    }

    :deep(.el-tabs__nav-wrap),
    :deep(.el-tabs__nav-scroll) {
      overflow: visible; /* 关键：Element Plus 默认这里可能是 hidden */
    }

    :deep(.el-tabs__nav) {
      display: flex;
      align-items: center;
      gap: 10px;
      border: none;
    }

    :deep(.el-tabs__item) {
      height: 32px;
      padding: 0 15px !important;
      border-radius: 6px;
      border: 1px solid var(--el-border-color) !important;
      box-sizing: border-box; /* 高度包含边框，避免溢出 */
      line-height: 30px;      /* 32px - 上下边框 2px */
    }
  }
}

</style>

