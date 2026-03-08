<template>
  <div class="layout-root">
    <aside class="sidebar page-card" :class="{ collapsed: isCollapsed }">
      <div class="brand brand-font">
        <div class="brand-mark">2C</div>
        <div v-if="!isCollapsed" class="brand-text">
          <strong>第二课堂系统</strong>
          <span>Second Score</span>
        </div>
      </div>

      <el-menu
        :default-active="activePath"
        class="menu"
        router
        :collapse="isCollapsed"
      >
        <el-menu-item v-for="item in menuRoutes" :key="item.path" :index="item.path">
          <el-icon>
            <component :is="item.meta.icon || 'Menu'" />
          </el-icon>
          <template #title>{{ item.meta.title }}</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="main-area">
      <header class="topbar page-card">
        <div class="left-actions">
          <el-button text @click="toggleCollapse" class="mobile-only">
            <el-icon><Expand /></el-icon>
          </el-button>
          <h1 class="brand-font">{{ currentTitle }}</h1>
        </div>
        <div class="right-actions">
          <el-upload
            class="avatar-upload"
            :show-file-list="false"
            accept="image/*"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <el-tooltip placement="bottom" content="点击上传头像（JPG/PNG/WebP，≤3MB）">
              <el-avatar :size="38" :src="avatarSrc">{{ avatarText }}</el-avatar>
            </el-tooltip>
          </el-upload>
          <el-tag type="info" effect="plain">{{ roleText }}</el-tag>
          <span class="username">{{ authStore.displayName }}</span>
          <el-button type="danger" plain @click="onLogout">退出登录</el-button>
        </div>
      </header>

      <section class="content-wrap page-card">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type UploadProps, type UploadRequestOptions } from 'element-plus'

import { uploadAvatarApi } from '@/api/auth'
import { appRoutes, hasRoutePermission } from '@/router/routes'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)

const menuRoutes = computed(() =>
  appRoutes.filter((item) => !item.meta.hideInMenu && hasRoutePermission(authStore.permissions, item.meta.perm))
)

const activePath = computed(() => route.path)

const currentTitle = computed(() => {
  const current = menuRoutes.value.find((item) => item.path === route.path)
  return current?.meta.title || '第二课堂学分系统'
})

const roleText = computed(() => {
  if (authStore.roles.includes('SYSTEM_ADMIN')) return '系统管理员'
  if (authStore.roles.includes('ACTIVITY_ADMIN')) return '活动管理员'
  if (authStore.roles.includes('STUDENT')) return '学生'
  return authStore.roles[0] || '未授权'
})

const avatarSrc = computed(() => authStore.avatarUrl || '')
const avatarText = computed(() => authStore.displayName.slice(-2))

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return false
  }
  const isLt3M = rawFile.size / 1024 / 1024 < 3
  if (!isLt3M) {
    ElMessage.error('头像大小不能超过 3MB')
    return false
  }
  return true
}

async function handleAvatarUpload(options: UploadRequestOptions) {
  try {
    const result = await uploadAvatarApi(options.file as File)
    authStore.setAvatar(result.avatarUrl)
    ElMessage.success('头像更新成功')
    options.onSuccess(result)
  } catch (error) {
    options.onError(error as any)
  }
}

function onLogout() {
  authStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.layout-root {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 270px 1fr;
  gap: 14px;
  padding: 14px;
}

.sidebar {
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding: 8px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: var(--surface);
  font-weight: 700;
  background: linear-gradient(135deg, var(--brand), color-mix(in oklch, var(--brand) 60%, var(--accent)));
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-text strong {
  font-size: 16px;
}

.brand-text span {
  font-size: 12px;
  color: var(--text-sub);
}

.menu {
  border-right: 0;
  background: transparent;
  flex: 1;
}

.sidebar.collapsed {
  width: 82px;
}

.main-area {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.topbar {
  min-height: 74px;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.left-actions h1 {
  margin: 0;
  font-size: clamp(20px, 2vw, 28px);
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-upload :deep(.el-upload) {
  border-radius: 999px;
  cursor: pointer;
  border: 1px solid color-mix(in oklch, var(--brand) 24%, var(--line));
  padding: 2px;
  background: color-mix(in oklch, var(--brand-soft) 38%, white);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.avatar-upload :deep(.el-upload:hover) {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px oklch(0.52 0.08 230 / 0.18);
}

.username {
  font-weight: 600;
}

.content-wrap {
  min-height: calc(100vh - 120px);
  overflow: auto;
}

.mobile-only {
  display: none;
}

@media (max-width: 1024px) {
  .layout-root {
    grid-template-columns: 1fr;
    padding: 8px;
  }

  .sidebar {
    order: 2;
    padding: 8px;
  }

  .main-area {
    order: 1;
  }

  .right-actions {
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .mobile-only {
    display: inline-flex;
  }
}
</style>
