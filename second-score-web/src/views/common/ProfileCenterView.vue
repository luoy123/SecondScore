<template>
  <div class="page-shell profile-page">
    <div class="page-title">
      <h2 class="brand-font">个人中心</h2>
      <el-button @click="loadProfile">刷新信息</el-button>
    </div>

    <div class="profile-grid">
      <section class="page-card profile-card">
        <h3 class="brand-font">基本信息</h3>
        <div class="avatar-row">
          <el-upload
            :show-file-list="false"
            accept="image/*"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <el-avatar :size="72" :src="authStore.avatarUrl">{{ authStore.displayName.slice(-2) }}</el-avatar>
          </el-upload>
          <div>
            <div class="name">{{ authStore.displayName }}</div>
            <div class="tip-text">点击头像可上传新头像（JPG/PNG/WebP，≤3MB）</div>
          </div>
        </div>

        <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="90px">
          <el-form-item label="用户名">
            <el-input v-model="profileForm.username" disabled />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="profileForm.studentNo" disabled />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="profileForm.realName" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profileForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="profileForm.email" />
          </el-form-item>
          <el-form-item label="学院">
            <el-input :model-value="collegeName(profileForm.collegeId)" disabled />
          </el-form-item>
          <el-form-item label="专业">
            <el-input :model-value="majorName(profileForm.majorId)" disabled />
          </el-form-item>
          <el-form-item label="班级">
            <el-input :model-value="className(profileForm.classId)" disabled />
          </el-form-item>
          <el-form-item label="账号状态">
            <el-tag :type="profileForm.status === 1 ? 'success' : 'info'">{{ profileForm.status === 1 ? '启用' : '停用' }}</el-tag>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存资料</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="page-card pwd-card">
        <h3 class="brand-font">修改密码</h3>
        <el-alert type="warning" show-icon :closable="false" title="修改密码后将自动退出，请使用新密码重新登录" />
        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="96px" style="margin-top: 14px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" show-password type="password" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" show-password type="password" />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" show-password type="password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingPassword" @click="savePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadProps, type UploadRequestOptions } from 'element-plus'

import { changePasswordApi, getProfileApi, updateProfileApi, uploadAvatarApi } from '@/api/auth'
import { listClassesApi, listCollegesApi, listMajorsApi, type ClassItem, type CollegeItem, type MajorItem } from '@/api/base'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const savingProfile = ref(false)
const savingPassword = ref(false)

const colleges = ref<CollegeItem[]>([])
const majors = ref<MajorItem[]>([])
const classes = ref<ClassItem[]>([])

const profileForm = reactive({
  username: '',
  studentNo: '',
  realName: '',
  phone: '',
  email: '',
  collegeId: undefined as number | undefined,
  majorId: undefined as number | undefined,
  classId: undefined as number | undefined,
  status: 1
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '新密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

function collegeName(id?: number) {
  if (!id) return '-'
  return colleges.value.find((i) => i.id === id)?.collegeName || `#${id}`
}

function majorName(id?: number) {
  if (!id) return '-'
  return majors.value.find((i) => i.id === id)?.majorName || `#${id}`
}

function className(id?: number) {
  if (!id) return '-'
  return classes.value.find((i) => i.id === id)?.className || `#${id}`
}

async function loadBase() {
  const [collegeData, majorData, classData] = await Promise.all([
    listCollegesApi(),
    listMajorsApi(),
    listClassesApi()
  ])
  colleges.value = collegeData
  majors.value = majorData
  classes.value = classData
}

async function loadProfile() {
  const profile = await getProfileApi()
  profileForm.username = profile.username
  profileForm.studentNo = profile.studentNo || ''
  profileForm.realName = profile.realName || ''
  profileForm.phone = profile.phone || ''
  profileForm.email = profile.email || ''
  profileForm.collegeId = profile.collegeId
  profileForm.majorId = profile.majorId
  profileForm.classId = profile.classId
  profileForm.status = profile.status
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

async function saveProfile() {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingProfile.value = true
  try {
    await updateProfileApi({
      realName: profileForm.realName.trim(),
      phone: profileForm.phone.trim() || undefined,
      email: profileForm.email.trim() || undefined
    })
    await authStore.fetchProfile()
    await loadProfile()
    ElMessage.success('个人资料已更新')
  } finally {
    savingProfile.value = false
  }
}

async function savePassword() {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingPassword.value = true
  try {
    await changePasswordApi({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    authStore.logout()
    router.replace('/login')
  } finally {
    savingPassword.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadBase(), loadProfile()])
})
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.profile-grid {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
  gap: 12px;
}

.profile-card,
.pwd-card {
  padding: 16px;
}

.profile-card h3,
.pwd-card h3 {
  margin: 0 0 12px;
  font-size: 18px;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.name {
  font-weight: 600;
  margin-bottom: 4px;
}

@media (max-width: 1080px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
