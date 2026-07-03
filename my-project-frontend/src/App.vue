<script setup>


import {useDark, useToggle} from "@vueuse/core";
import {onMounted, provide, ref} from "vue";
import {isUnauthorized} from "@/net";
import {apiUserInfo} from "@/net/api/User";

useDark({
  selector:'html',
  attribute:'class',
  valueDark:'dark',
  valueLight:'light'
})

useDark({
  onChanged(dark){useToggle(dark)}
})

const loading = ref(false)
provide('userLoading',loading)
onMounted(() => {
  if(!isUnauthorized()){
    apiUserInfo(loading)
  }
})
</script>
<template>
  <header>
    <div class="wrapper">
      <router-view/>
    </div>
  </header>
</template>

<style scoped>
.wrapper{
  line-height: 1.5;
}
</style>
