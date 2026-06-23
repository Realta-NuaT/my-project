
<script setup>
import {Check, Document} from "@element-plus/icons-vue"
import {computed, reactive, ref} from "vue"
import {QuillEditor} from "@vueup/vue-quill"
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios"
import {accessHeader, get, post} from "@/net/index.js"
import {ElMessage} from "element-plus"

import { Quill } from '@vueup/vue-quill'  // 需要先导入 Quill
import ImageResize from 'quill-image-resize-vue'
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store";
Quill.register('modules/imageResize', ImageResize)
const store = useStore();

defineProps({
  show: Boolean,
})

const emit = defineEmits(['close'],['success'])

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false
})

function initEditor() {
  if (!quillRef.value) return
  quillRef.value.setContents('','user')
  editor.title = ''
  editor.type = null
}

function deltaToText(delta) {
  if (!delta?.ops) return ""
  let str = ""
  for (let op of delta.ops) {
    if (typeof op.insert === 'string') {
      str += op.insert
    }
    // 图片、视频、公式等非文本 insert 一律忽略
  }
  return str.replace(/\s/g, "")
}

const contentLength = computed(()=>deltaToText(editor.text).length)

function submitTopic(){
  const text = deltaToText(editor.text)
  if(text.length > 20000) {
    ElMessage.warning('字数超出限制，无法发布主题！')
    return
  }
  if(!editor.title) {
    ElMessage.warning('请填写标题！')
    return
  }
  if(!editor.type) {
    ElMessage.warning('请选择一个合适的帖子类型！')
    return
  }
  post('/api/forum/create-topic',{
    type:editor.type.id,
    title:editor.title,
    content:editor.text
  },()=>{
    ElMessage.success("帖子发表成功")
    emit('success')
      }
  )
}

const insertImageToEditor = async (file) => {
  const formData = new FormData()
  formData.append('file', file)

  try {
    editor.loading = true

    const res = await axios.post('/api/image/cache', formData, {
      headers: {
        ...accessHeader(),
        'Content-Type': 'multipart/form-data'
      },
      validateStatus: () => true
    })

    const data = res.data

    if (data.code === 200) {
      const url = axios.defaults.baseURL + '/images' + data.data

      const quill = quillRef.value?.getQuill?.()
      if (!quill) {
        ElMessage.error('编辑器未初始化')
        return
      }

      const range = quill.getSelection(true) || {index: quill.getLength()}
      quill.insertEmbed(range.index, 'image', url)
      quill.setSelection(range.index + 1)

      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(data.msg || '图片上传失败')
    }
  } finally {
    editor.loading = false
  }
}

const uploadImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.click()

  input.onchange = async () => {
    const file = input.files[0]
    if (!file) return
    await insertImageToEditor(file)
  }
}

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {background: []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]},
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: "-1"}, {indent: "+1"}
      ]
      // 删除了 handlers，统一在 onQuillReady 中设置
    },
    imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]  // DisplaySize 会在图片下方显示当前尺寸，不需要可移除
    }
  }
}

const quillRef = ref(null)

const onQuillReady = () => {
  const quill = quillRef.value?.getQuill?.()
  if (!quill) return

  const Delta = quill.constructor.import('delta')

  // 禁止从剪贴板粘贴 HTML 中的 <img> 标签，避免双图
  quill.clipboard.addMatcher('img', () => new Delta())

  // 覆盖工具栏图片按钮行为
  const toolbar = quill.getModule('toolbar')
  toolbar.handlers.image = uploadImage

  // 捕获粘贴事件，处理图片文件（比 Quill 默认粘贴更早执行）
  quill.root.addEventListener('paste', async (e) => {
    const file = [...(e.clipboardData?.items || [])]
        .find(i => i.type.startsWith('image/'))
        ?.getAsFile()

    if (file) {
      e.preventDefault()
      e.stopPropagation()
      await insertImageToEditor(file)
    }
  }, true)
}
</script>

<template>
  <div>
    <el-drawer :model-value="show"
               direction="btt"
               @open="initEditor"
               :close-on-click-modal="false"
               :size="650"
               @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前,请遵守相应法律法规,不要出现骂人等粗口的不文明行为</div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 150px">
          <el-select placeholder="选择主题类型..." value-key="id" v-model="editor.type" :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span style="margin-left: 10px">{{item.name}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                    maxlength="30"
          />
        </div>
      </div>
      <div style="margin-top: 5px;font-size: 13px;color: grey">
        <color-dot :color="editor.type?editor.type.color:'#d8d8d8'"/>
        <span style="margin-left: 5px">{{editor.type?editor.type.desc:'请在上方选择一个帖子类型!'}}</span>
      </div>
      <div style="margin-top: 10px;height: 440px;overflow: hidden;border-radius: 5px "
           v-loading="editor.loading" element-loading-text="正在上传图片,请稍后...">
        <quill-editor v-model:content="editor.text"
                      @update:content="val => editor.text = val"
                      style="height: calc(100% - 45px)"
                      content-type="delta"
                      placeholder="今天想分享点什么呢?"
                      ref="quillRef"
                      @ready="onQuillReady"
                      :options="editorOption"/>
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <div style="color: grey ; font-size: 13px">
          当前字数 {{ contentLength }} (最大支持20000字)
        </div>
        <div>
          <el-button type="success" :icon="Check" @click="submitTopic" plain>立即发表主题</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer){
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.ql-toolbar){
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}
:deep(.ql-container){
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}
:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
:deep(.ql-editor){
  font-size: 14px;
}
:deep(.ql-editor img) {
  max-width: 300px;   /* 可改成你想要的宽度，比如 200px */
  height: auto;
}
</style>