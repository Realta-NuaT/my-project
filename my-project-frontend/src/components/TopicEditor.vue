<script setup>
import { Check, Document } from "@element-plus/icons-vue"
import { reactive } from "vue"
import { QuillEditor } from "@vueup/vue-quill"
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios"
import { accessHeader } from "@/net/index.js"
import { ElMessage } from "element-plus"

defineProps({
  show: Boolean,
})

const emit = defineEmits(['close'])

const editor = reactive({
  type: null,
  title: '',
  text:'',
  loading: false
})

const types = [
  {id: 1, name: '日常闲聊', desc: '在这里分享你的各种日常'},
  {id: 2, name: '真诚交友', desc: '在校园里寻找与自己志同道合的朋友'},
  {id: 3, name: '问题反馈', desc: '反馈你在校园里遇到的问题'},
  {id: 4, name: '恋爱官宣', desc: '向大家展示你的恋爱成果'},
  {id: 5, name: '踩坑记录', desc: '将你遇到的坑分享给大家, 防止其他人再次入坑'},
]

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

      const range = quill.getSelection(true) || { index: quill.getLength() }
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

const uploadImage = function () {
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
        { color: [] }, { background: [] },
        { size: ["small", false, "large", "huge"] },
        { header: [1, 2, 3, 4, 5, 6, false] },
        { list: "ordered" }, { list: "bullet" }, { align: [] },
        "blockquote", "code-block", "link", "image",
        { indent: "-1" }, { indent: "+1" }
      ],
      handlers: {
        image: function () {
          uploadImage()
        }
      }
    }
  }
}

import { ref, onMounted } from "vue"

const quillRef = ref(null)

let quillInstance = null
const onQuillReady = () => {
  const quill = quillRef.value?.getQuill?.()
  if (!quill) return

  quillInstance = quill
  const Delta = quill.constructor.import('delta')

// ❗彻底禁止 image delta 插入
  quill.clipboard.addMatcher('img', () => {
    return new Delta()
  })

  quill.clipboard.addMatcher(Node.ELEMENT_NODE, (node, delta) => {
    const newDelta = new Delta()

    delta.ops.forEach(op => {
      if (op.insert && op.insert.image) return
      newDelta.push(op)
    })

    return newDelta
  })

  const toolbar = quill.getModule('toolbar')
  toolbar.handlers.image = uploadImage

  quill.root.addEventListener('paste', async (e) => {
    const file = [...(e.clipboardData?.items || [])]
        .find(i => i.type.startsWith('image/'))
        ?.getAsFile()

    if (file) {
      // 彻底阻止事件继续传播（不会再到 Quill 的冒泡监听）
      e.preventDefault()
      e.stopPropagation()          // 捕获阶段用 stopPropagation 即可
      await insertImageToEditor(file)
    }
    // 如果没有图片文件，什么都别做，让 Quill 按默认方式处理文本/HTML
  }, true)  // ← 关键：捕获阶段
}

</script>

<template>
  <div>
    <el-drawer :model-value="show"
               direction="btt"
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
          <el-select placeholder="选择主题类型..." v-model="editor.type">
            <el-option v-for="item in types" :label="item.name" :value="item.id"/>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"/>
        </div>
      </div>
      <div style="margin-top: 15px;height: 460px;overflow: hidden;border-radius: 5px "
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
          当前字数 666 (最大支持20000字)
        </div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发表主题</el-button>
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