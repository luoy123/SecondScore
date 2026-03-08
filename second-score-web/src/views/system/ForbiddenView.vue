<template>
  <div class="forbidden-wrap page-shell">
    <div class="forbidden-box page-card">
      <h2 class="brand-font">无权限访问</h2>
      <p>当前账号没有访问该页面的权限，请联系系统管理员。</p>
      <el-button type="primary" @click="back">返回可访问页面</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { resolveHomePath } from '@/router/routes'

const router = useRouter()
const authStore = useAuthStore()

function back() {
  router.replace(resolveHomePath(authStore.permissions))
}
</script>

<style scoped>
.forbidden-wrap {
  min-height: 100%;
  display: grid;
  place-items: center;
}

.forbidden-box {
  max-width: 520px;
  padding: 28px;
  text-align: center;
}

.forbidden-box h2 {
  margin: 0 0 8px;
}

.forbidden-box p {
  color: var(--text-sub);
  margin: 0 0 18px;
}
</style>
