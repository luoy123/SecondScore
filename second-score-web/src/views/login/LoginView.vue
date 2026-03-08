<template>
  <div class="login-page">
    <div class="bg-image"></div>
    <div class="bg-mask"></div>
    <div class="decor decor-left"></div>
    <div class="decor decor-right"></div>

    <section class="login-card page-card">
      <div class="card-head">
        <p class="brand-font small">高校第二课堂管理系统</p>
        <h1 class="brand-font">第二课堂学分统计与分析平台</h1>
        <span>统一完成活动发布、报名审核、参与登记、学分认定与数据分析</span>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" size="large" placeholder="请输入用户名" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password size="large" placeholder="请输入密码" clearable>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="captchaCode">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" size="large" placeholder="请输入验证码" clearable>
              <template #prefix><el-icon><Key /></el-icon></template>
            </el-input>
            <button class="captcha-image" type="button" :disabled="captchaLoading" @click="refreshCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="captcha" />
              <span v-else>加载中</span>
            </button>
            <el-button circle :icon="RefreshRight" :disabled="captchaLoading" @click="refreshCaptcha" />
          </div>
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="onSubmit">登录系统</el-button>
      </el-form>

    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'

import { getCaptchaApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { resolveHomePath } from '@/router/routes'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const captchaLoading = ref(false)
const captchaImage = ref('')
const formRef = ref<FormInstance>()

const form = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaId: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function refreshCaptcha() {
  captchaLoading.value = true
  try {
    const result = await getCaptchaApi()
    form.captchaId = result.captchaId
    form.captchaCode = ''
    captchaImage.value = result.imageBase64
  } finally {
    captchaLoading.value = false
  }
}

async function onSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!form.captchaId) {
    await refreshCaptcha()
    ElMessage.warning('验证码加载中，请稍后重试')
    return
  }

  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.replace(resolveHomePath(authStore.permissions))
  } catch {
    await refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 16px;
  overflow: hidden;
}

.bg-image {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(112deg, color-mix(in oklch, var(--brand) 44%, transparent), transparent 58%),
    url("https://images.unsplash.com/photo-1486551937199-baf066858de7?auto=format&fit=crop&w=1920&q=80");
  background-size: cover;
  background-position: center;
  transform: scale(1.04);
  z-index: 0;
}

.bg-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(145deg, oklch(0.2 0.03 225 / 0.32), oklch(0.34 0.04 240 / 0.18));
  z-index: 0;
}

.decor {
  position: absolute;
  border-radius: 999px;
  filter: blur(80px);
  z-index: 1;
}

.decor-left {
  width: 340px;
  height: 340px;
  background: color-mix(in oklch, var(--brand) 24%, white);
  left: -90px;
  top: -80px;
}

.decor-right {
  width: 380px;
  height: 380px;
  background: color-mix(in oklch, var(--accent) 20%, white);
  right: -120px;
  bottom: -120px;
}

.login-card {
  z-index: 2;
  width: min(94vw, 470px);
  padding: clamp(18px, 3.2vw, 34px);
  backdrop-filter: blur(5px);
  border-color: color-mix(in oklch, var(--line) 55%, white);
}

.card-head {
  margin-bottom: 18px;
}

.card-head .small {
  margin: 0;
  font-size: 13px;
  color: color-mix(in oklch, var(--brand) 80%, black);
  letter-spacing: 0.8px;
}

.card-head h1 {
  margin: 8px 0 8px;
  font-size: clamp(24px, 3.2vw, 34px);
  line-height: 1.2;
}

.card-head span {
  color: var(--text-sub);
  font-size: 14px;
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 112px auto;
  gap: 8px;
  align-items: center;
}

.captcha-image {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 0;
  background: var(--surface);
  height: 40px;
  overflow: hidden;
  cursor: pointer;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.captcha-image span {
  display: grid;
  place-items: center;
  height: 100%;
  color: var(--text-sub);
  font-size: 12px;
}

.submit-btn {
  width: 100%;
  margin-top: 6px;
  border-radius: 12px;
}

@media (max-width: 520px) {
  .captcha-row {
    grid-template-columns: 1fr;
  }

  .captcha-image {
    height: 44px;
  }
}
</style>
