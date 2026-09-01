<script setup>

</script>

<template>
<div style="height: 100vh;width: 100vw;overflow: hidden;display: flex">
  <div style="flex: 1;background-color: black">
    <el-image style="width:100%;height: 100%" fit="cover"
        src="https://element-plus.org/images/element-plus-logo.svg"/>
  </div>
  <div class="welcome-title">
    <div style="font-size: 30px;font-weight: bold">欢迎访问</div>
    <div style="margin-top: 10px">使用SpringBoot3 + Vue3编写的前后端分离项目,结合Jwt实验校验方案</div>
      <div style="margin-top: 10px;margin-bottom: 5px;font-size: 20px;font-weight: bold">前端</div>
      <div>
          采用VueRouter进行路由分配,通过UserStore存储用户与帖子类型基础信息,并且采用Axios作为异步请求框架,
          采用element-plus最为UI插件库, 并且采用VueUse进行深色模式适配,同时将前后端交互的接口进行整合,将接口整
          合在JS文件中按照Ai,Forum,Email User,Index进行规范分类处理,同时还将页面进行模块化设计,避免单一文件冗
          余,将插件与主页面次页面分类管理
      </div>
      <div style="margin-top: 10px;margin-bottom: 5px;font-size: 20px;font-weight: bold">后端</div>
      <div>
          采用SpringMVC框架对后端项目排布进行处理;结合DeepSeek大模型,处理用户交互信息;采用Mybatis-Plus作为持久层框架,便携处理存储
          服务;使用Redis进行存储缓存, 处理例如注册或者是操作验证码,天气 信息缓存,帖子信息缓存,IP请求次数限流等等 ,降低前端查询后端时,
          后端服务器的压力;使 用RabbitMQ处理堆积的短信发送 功能,使用监听 器处理发送发送邮件与错误未能发出的邮件,避免错误未能发出的邮件
          堆积Rabbit MQ,降低服务负担;采用 SpringSecurity 作为权限校验框架，手动整合Jwt校验方案,将安全信息,与限流操作整合在applica
          ti on.yml中,方便后 端统一处理限制信息;同时视图层对象和数据层对象分离，设置DTO层,ES层与VO层,编写工具方法利用反射快速互相转换
          ;错误和异常 页面统一采用JSON格式返回，前端处理响应更统一;手动处理跨域，采用过滤器实现;采用过滤器实现对所有请求自动生成
          雪花ID方便线上定位问题;针对于多环境进行处理，开发环境和生产环境采用不同的配置,将其application-dev.yml(开发环境)与
          application-prod.yml(生产环境)分离处理,日志中包含单次请求完整信息以及对应的雪花ID，支持文件记录;项目整体结构
          清晰，每个文件职责明确;针对与帖子搜索,由于处理大规模复杂数据,将MySQL与es8进行映射处理,采用Alibaba的canal进行
          连接处理,并且配置了Admin端口的图形化监控窗口,配置es8后将查询数据进行处理返回(包括高亮处理);对于大文件(本项目目
          前只涉及图片),为避免增加服务器作为图床的压力,采用minio进行存储,服务器只需要生成并存储管理url,返回前端进行调用即可;
          对于Api接口部分,对管理端(admin)与其他端口(exception)进行分类管理,确保安全控制,并且接口部分只实现简单的接口交互处理,
          功能实现部分交由Service层进行实现;针对返回前端的信息,采用记录文本,进行统一处理,设置RestBean与PageBean进行返回代码与
          data部分的处理,以及错误信息的返回;针对过滤器部分,将接口访问限制,路由访问次数限制,JWT验证限制以及用于记录所有用户请求信息
          的过滤器;
      </div>
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
    left: 30px;
    right: 430px; /* 给右侧 400px + 30px 间距 */
    bottom: 30px;

    color: aliceblue;
    text-shadow: 0 0 10px white;

    box-sizing: border-box;

    white-space: normal;
    overflow-wrap: anywhere;
    word-break: break-word;
}
</style>