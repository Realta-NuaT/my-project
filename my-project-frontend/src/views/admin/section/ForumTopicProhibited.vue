<script setup>

import {apiForumProhibit, apiForumProhibitedList} from "@/net/api/forum";
import {ElMessage} from "element-plus";
import {ref} from "vue";

const prohibitedWords = ref('')

const saveProhibitedList = () => {
    const list = prohibitedWords.value.split(',')
    apiForumProhibit(list, () => ElMessage.success("违禁词更新成功"))
}

apiForumProhibitedList( data => prohibitedWords.value = data.join(',') )
</script>

<template>
    <div class="prohibited-input">
        <div class="title">违禁词管理</div>
        <div class="desc">所有存在违禁词的帖子都将会被限制发布,使用,隔开</div>
        <el-input type="textarea" :rows="8" v-model="prohibitedWords"/>
        <div style="text-align: right; margin-top: 20px">
            <el-button @click="saveProhibitedList" type="primary">保存违禁词列表</el-button>
        </div>
    </div>
</template>

<style lang="less" scoped>
.prohibited-input{
    margin-top: 50px;
}
</style>