<script setup>
import {Notebook, Plus} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {apiTopicTypeCreate, apiTopicTypeDelete, apiTopicTypeUpdate} from "@/net/api/forum";
import {ElMessage, ElMessageBox} from "element-plus";

const props = defineProps({
    types:Array,
})

const findType = (type) =>  props.types.find(item=>item.id === type)

const emit = defineEmits(['update'])

const editor = reactive({
    display:false,
    target:null,
    type:'update'
})

const startEdit = (target) => {
    editor.target = { ...target }
    editor.type= 'update'
    editor.display = true
}

const createTopic = () => {
    editor.target = { name: '', desc: '', color: ''}
    editor.type= 'create'
    editor.display = true
}

const updateOrCreateType = () => {
    if(editor.type==='create'){
        apiTopicTypeCreate(editor.target, data => {
            ElMessage.success("创建成功")
            editor.display = false
            emit('update')
        })
    }else{
        apiTopicTypeUpdate(editor.target, data => {
            ElMessage.success("更新成功")
            editor.display = false
            emit('update')
        })
    }
}

const deleteType = (id) => {
    ElMessageBox.confirm('删除后将永远无法恢复,您确定要删除吗?','删除帖子分类',{
        callback: action =>{
            if(action === 'confirm'){
                apiTopicTypeDelete(id, () => {
                    ElMessage.success('删除成功')
                    emit('update')
                })
            }
        }
    })
}

</script>

<template>
    <div>
        <div class="topic-type-header">
            <div>
                <div class="title">
                    <el-icon><Notebook/></el-icon>
                    论坛帖子类型列表管理
                </div>
                <div class="desc">
                    在这里管理论坛的所有帖子类型
                </div>
            </div>
            <el-button type="primary" :icon="Plus" @click="createTopic">新建</el-button>
        </div>
        <el-table :data="types" height="400">
            <el-table-column prop="id" label="类型ID" width="80" align="center"/>
            <el-table-column prop="name" label="类型名称" width="200" align="center">
                <template #default="{ row }">
                    <div class="topic-type">
                        <div class="type-dot" :style="{ backgroundColor: findType(row.id)?.color ?? 'grey ' }"/>
                        <div>{{ findType(row.id)?.name ?? '未知类型' }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="desc" label="类型介绍" min-width="400" show-overflow-tooltip/>
            <el-table-column label="操作" width="150" fixed="right" align="center">
                <template #default="{ row }">
                    <el-button size="small" type="info" @click="startEdit(row)" plain>编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteType(row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog title="编辑帖子" width="400" v-model="editor.display">
            <el-form label-position="top">
                <el-form-item label="类型ID">
                    <el-input v-model="editor.target.id" disabled></el-input>
                </el-form-item>
                <el-form-item label="类型名称">
                    <div style="display: flex;">
                        <el-input v-model="editor.target.name" style="width: 100%"></el-input>
                        <el-color-picker v-model="editor.target.color"/>
                    </div>
                </el-form-item>
                <el-form-item label="类型介绍">
                    <el-input v-model="editor.target.desc" type="textarea" :rows="7"></el-input>
                </el-form-item>
                <div style="text-align: right">
                    <el-button @click="updateOrCreateType" type="primary">保存</el-button>
                </div>
            </el-form>
        </el-dialog>
    </div>
</template>

<style scoped>
.topic-type-header{
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
        font-weight: bold;
    }

    .desc {
        color: #bababa;
        font-size: 13px;
        margin-bottom: 20px;
    }
}

.topic-type {
    display: flex;
    align-items: center;
    gap: 10px;
    width: fit-content;
    margin: auto;
    .type-dot{
        height: 7px;
        width: 7px;
        border-radius: 50%;
    }
}
</style>