<script setup>
defineProps({
  color: String
})
</script>

<template>
  <div class="dot" :style="{ backgroundColor: color }"></div>
</template>

<style scoped>
.dot{
  width: 10px;
  height: 10px;
  border-radius: 5px;
  display: inline-block;
}
</style>