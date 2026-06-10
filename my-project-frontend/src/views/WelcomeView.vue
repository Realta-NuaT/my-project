<script setup>

</script>

<template>
<div style="height: 100vh;width: 100vw;overflow: hidden;display: flex">
  <div style="flex: 1;background-color: black">
    <el-image style="width:100%;height: 100%" fit="cover"
        src="https://element-plus.org/images/element-plus-logo.svg"/>
  </div>
  <div class="welcome-title">
    <div style="font-size: 30px;font-weight: bold">欢迎造访</div>
    <div style="margin-top: 10px">使用java语言</div>
    <div style="margin-top: 5px">巴拉巴拉</div>
  </div>
  <div class="right-card">
    <router-view v-slot="{ Component }" >
      <transition name="el-fade-in-linear" mode="out-in">
        <component :is="Component"/>
      </transition>
    </router-view>
  </div>
</div>
</template>

<style scoped>
.right-card{
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}
.welcome-title{
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: aliceblue;
  text-shadow: 0 0 10px white;
}
</style>