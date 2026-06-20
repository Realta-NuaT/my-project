<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const weather = reactive({
  location: {},
  now:{},
  hourly:[],
  success: false
})
const editor = ref(false)

const today = computed(()=>{
  const date = new Date();
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
})


navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data =>{
    Object.assign(weather, data)
    weather.success = true;
  })
  }, error =>{
    console.log(error);
    switch (error.code) {
      case error.PERMISSION_DENIED:
        ElMessage.warning('未允许获取位置信息，已使用默认位置')
        break
      case error.TIMEOUT:
        ElMessage.warning('定位超时，已使用默认位置')
        break
      default:
        ElMessage.warning('定位失败，已使用默认位置')
    }
    get('/api/forum/weather?longitude=118.280350&latitude=35.121960',data => {
      Object.assign(weather, data)
      weather.success = true;
    })
  },{
  timeout: 8000, // 延长超时到8秒，减少误判
  enableHighAccuracy: true, // 高精度，提升定位精确率
  maximumAge: 300000 // 允许复用5分钟内缓存定位
})

</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px;">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><edit-pen/></el-icon>点击发表主题...
        </div>
      </light-card>
      <light-card style="margin-top: 10px;height: 30px"></light-card>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px;">
        <light-card style="height: 150px" v-for="item in 10"></light-card>
      </div>
    </div>
    <div style="width: 280px">
      <div style="position: sticky;top:20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon style="translate: 0 2px"><CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;margin: 10px;color: grey">
            全球领先的中文搜索引擎、致力于让网民更便捷地获取信息，找到所求。
            百度超过千亿的中文网页数据库，可以瞬间找到相关的搜索结果。 地图
            浏览地图、搜索地点、查询公交驾车线路、查看实时路况，您的出行指南
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon style="translate: 0 2px"><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <Weather :data="weather"/>
        </light-card>
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon><Link/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid;grid-template-columns: repeat(2,1fr);grid-gap: 10px ;margin-top: 10px">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/js-design-banner.jpg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/vform-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/element-plus-logo.svg"/>
          </div>
        </div>
      </div>
    </div>
    <topic-editor :show="editor" @close="editor = false"/>
  </div>
</template>

<style lang="less" scoped>
.info-text{
  display:flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}
.friend-link{
  border-radius: 5px;
  overflow: hidden;
}
.create-topic{
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;
  &:hover{
    cursor: pointer;
  }
}
</style>