<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  ArrowRight, ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
const store = useStore();

const isLoading = ref(false)

const weather = reactive({
  location: {},
  now:{},
  hourly:[],
  success: false
})
const editor = ref(false)
const topics = reactive({
  list:[],
  type:0,
  page:0,
  end:false,
  top:[]
})

const collects = ref(false);

watch(()=>topics.type,()=>resetList(),{immediate:true})

const today = computed(()=>{
  const date = new Date();
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
})


get('api/forum/top-topic',data => topics.top = data)



function updateList(){
  if (topics.end || isLoading.value) return  // 正在加载则直接退出
  isLoading.value = true

  get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data=> {
    if(data != null){
      data.forEach(d => topics.list.push(d))
      topics.page++

    }
    if(!data || data.length < 10){
      topics.end = true
    }
        isLoading.value = false
  },
    () => {
      // 可以加一个错误回调
      isLoading.value = false
    }
  )
}

function onTopicCreate(){
  editor.value = false
  resetList()

}
function resetList(){
  topics.page = 0
  topics.end = false
  topics.list = []
  updateList()
}


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
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px;padding: 0 20px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><edit-pen/></el-icon>点击发表主题...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit /></el-icon>
          <el-icon><Document /></el-icon>
          <el-icon><Compass /></el-icon>
          <el-icon><Picture /></el-icon>
          <el-icon><Microphone /></el-icon>
        </div>
      </light-card>
      <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleDateString()}}</div>
        </div>
      </light-card>
      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="topics.type = item.id"
        >
             <color-dot :color="item.color" />
             <span style="margin-left: 5px">{{item.name}}</span>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px;"
               v-infinite-scroll="updateList">
            <light-card v-for="item in topics.list" class="topic-card"
                        @click="router.push('/index/topic-detail/'+item.id)">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div style="font-size: 13px;font-weight: bold">{{item.username}}</div>
                  <div style="font-size: 12px;color: grey">
                    <el-icon><Clock/></el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{new Date(item.time).toLocaleString()}}
                    </div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <topic-tag :type="item.type" />
                <span style="font-weight: bold;margin-left: 7px">{{item.title}}</span>
              </div>
              <div class="topic-content">{{item.text}}</div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"/>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle"><CircleCheck /></el-icon> {{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star /></el-icon> {{item.collect}}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 280px">
      <div style="position: sticky;top:20px">
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon> 查看我的收藏</span>
            <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>
        <light-card style="margin-top: 10px;">
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
    <topic-editor :show="editor" @success="onTopicCreate" @close="editor = false"/>
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>
</template>

<style lang="less" scoped>
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover{
    cursor: pointer;
    opacity: 0.6;
  }
}


.top-topic{
  display: flex;

  div:first-of-type{
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.7;
    transition: color .3s;

    &:hover{
      color: grey;
    }
  }
  div:nth-of-type(2){
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover{
    cursor: pointer;
  }
}

.type-select-card{
  background-color: #f5f5f5;
  padding:  2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;
  &.active{
    border:solid 1px #ead4c4;
  }

  &:hover{
    cursor: pointer;
    background-color: #dadada;
  }
}

.topic-card {
  padding: 15px;
  transition:  scale .3s;

  &:hover{
    scale: 1.015;
    cursor: pointer;
  }
  .topic-content{
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }


  .topic-image{
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}
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
.dark {
  .create-topic{
    background-color: #1e1d1d;
  }
  .type-select-card{
    background-color: #282828;

    &.active{
      border: solid 1px #64594b;
      &:hover{
        background-color: #5e5e5e;
      }
    }
  }
}
</style>