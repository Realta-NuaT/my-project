<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill"
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";
const props = defineProps({
  show:Boolean,
  tid:String,
  quote:Object,
})
const emit = defineEmits(["close", 'comment'])

const init = () => content.value = new Delta()

const content = ref()

function submitComment() {
  post('api/forum/add-comment', {
    tid:props.tid,
    quote:props.quote ? props.quote.id : -1,
    content: JSON.stringify(content.value),
  },() =>{
    ElMessage.success('发表评论成功')
    emit('comment')
  })
}

function deltaToSimpleText(delta) {
  let str = ''
  for (let op of JSON.parse(delta).ops) {
    str += op.insert
  }
  if(str.length > 35) str = str.substring(0, 35) + '...'
  return str
}

</script>

<template>
  <div>
    <el-drawer :model-value="show"
               :title="quote ? `发表对评论: ${deltaToSimpleText(quote.content)} 的回复` : '发表帖子回复'"
               @open="init"
               @close="emit('close')"
               direction="btt" :size="270"
               :close-on-click-modal="false"
    >
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请发表友善的评论,不要使用脏话骂人,都是大学生,素质高一点"/>
        </div>
        <div style="margin-top: 10px;text-align: right">
          <el-button type="success" @click="submitComment" plain>发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer){
  width: 800px;
  margin: 20px auto;
  border-radius: 10px;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.el-drawer__body){
  padding: 10px;
}
</style>