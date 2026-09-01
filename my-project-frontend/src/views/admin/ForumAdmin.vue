<script setup>



import ForumTopicProhibited from "@/views/admin/section/ForumTopicProhibited.vue";
import ForumTopicAdmin from "@/views/admin/section/ForumTopicAdmin.vue";
import {apiForumTypes} from "@/net/api/forum";
import {ref} from "vue";
import ForumTopicTypeAdmin from "@/views/admin/section/ForumTopicTypeAdmin.vue";

const types = ref([])

const initType = () => apiForumTypes(data => types.value = data)

initType()

</script>

<template>
  <div class="forum-admin">
      <div>
          <forum-topic-admin :types="types"/>
      </div>
      <forum-topic-type-admin :types="types" @update="initType"/>
      <forum-topic-prohibited/>
  </div>
</template>

<style lang="less" scoped>
.forum-admin{
    display: flex;
    flex-direction: column;
    gap: 50px;
}
</style>