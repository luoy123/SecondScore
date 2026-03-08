<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">活动管理</h2>
      <el-space>
        <el-input v-model="query.title" placeholder="活动标题" style="width: 210px" clearable />
        <el-select v-model="query.termId" clearable placeholder="学期" style="width: 160px">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已完结" value="FINISHED" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button type="success" @click="openCreate">新建活动</el-button>
      </el-space>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="title" label="活动名称" min-width="180" />
      <el-table-column prop="categoryName" label="类别" width="120" />
      <el-table-column prop="termName" label="学期" width="130" />
      <el-table-column prop="organizerName" label="负责人" width="120" />
      <el-table-column prop="credit" label="学分" width="90" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">{{ activityStatusLabel(scope.row.status) }}</template>
      </el-table-column>
      <el-table-column label="活动时间" min-width="170">
        <template #default="scope">{{ formatDateTime(scope.row.startTime) }} ~ {{ formatDateTime(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)" :disabled="scope.row.status !== 'DRAFT'">编辑</el-button>
          <el-button type="success" link @click="publish(scope.row.id)" :disabled="scope.row.status !== 'DRAFT'">发布</el-button>
          <el-button type="warning" link @click="finish(scope.row.id)" :disabled="scope.row.status !== 'PUBLISHED'">完结</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新建活动'" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="活动名称" prop="title"><el-input v-model="form.title" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="类别" prop="categoryId"><el-select v-model="form.categoryId" style="width: 100%"><el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="学期" prop="termId"><el-select v-model="form.termId" style="width: 100%"><el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" /></el-select></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="organizerId">
              <el-select v-model="form.organizerId" clearable style="width: 100%" :disabled="!canSelectOrganizer">
                <el-option v-for="u in organizers" :key="u.id" :label="`${u.realName}(${u.username})`" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="活动开始" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="活动结束" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="报名开始" prop="signupStart"><el-date-picker v-model="form.signupStart" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报名结束" prop="signupEnd"><el-date-picker v-model="form.signupEnd" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="人数上限" prop="capacity"><el-input-number v-model="form.capacity" :min="1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学分" prop="credit"><el-input-number v-model="form.credit" :min="0" :step="0.5" :precision="2" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="活动说明"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { createActivityApi, finishActivityApi, listActivitiesApi, publishActivityApi, updateActivityApi, type ActivityItem, type ActivitySaveParams } from '@/api/activity'
import { listCategoriesApi, listTermsApi, type CategoryItem, type TermItem } from '@/api/base'
import { listUsersApi, type UserItem } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { activityStatusLabel } from '@/utils/enumLabel'
import { formatDateTime } from '@/utils/format'

const authStore = useAuthStore()
const canSelectOrganizer = computed(() =>
  authStore.permissions.includes('activity:manage:all') && authStore.permissions.includes('user:manage')
)

const loading = ref(false)
const saving = ref(false)
const list = ref<ActivityItem[]>([])
const terms = ref<TermItem[]>([])
const categories = ref<CategoryItem[]>([])
const organizers = ref<UserItem[]>([])

const query = reactive({
  title: '',
  termId: undefined as number | undefined,
  status: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number>()
const formRef = ref<FormInstance>()
const form = reactive({
  title: '',
  categoryId: undefined as number | undefined,
  termId: undefined as number | undefined,
  organizerId: undefined as number | undefined,
  startTime: '',
  endTime: '',
  signupStart: '',
  signupEnd: '',
  capacity: 100,
  credit: 1,
  location: '',
  description: ''
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择类别', trigger: 'change' }],
  termId: [{ required: true, message: '请选择学期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择活动开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择活动结束时间', trigger: 'change' }],
  signupStart: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
  signupEnd: [{ required: true, message: '请选择报名结束时间', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入人数上限', trigger: 'change' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'change' }]
}

function resetForm() {
  form.title = ''
  form.categoryId = undefined
  form.termId = undefined
  form.organizerId = undefined
  form.startTime = ''
  form.endTime = ''
  form.signupStart = ''
  form.signupEnd = ''
  form.capacity = 100
  form.credit = 1
  form.location = ''
  form.description = ''
}

async function loadList() {
  loading.value = true
  try {
    list.value = await listActivitiesApi({
      title: query.title || undefined,
      termId: query.termId,
      status: query.status || undefined
    })
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  const [termData, categoryData] = await Promise.all([listTermsApi(), listCategoriesApi()])
  terms.value = termData
  categories.value = categoryData
  if (canSelectOrganizer.value) {
    const users = await listUsersApi()
    organizers.value = users.filter((u) => u.roles.includes('ACTIVITY_ADMIN'))
  }
}

function openCreate() {
  isEdit.value = false
  currentId.value = undefined
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: ActivityItem) {
  isEdit.value = true
  currentId.value = row.id
  form.title = row.title
  form.categoryId = row.categoryId
  form.termId = row.termId
  form.organizerId = row.organizerId
  form.startTime = row.startTime
  form.endTime = row.endTime
  form.signupStart = row.signupStart
  form.signupEnd = row.signupEnd
  form.capacity = row.capacity
  form.credit = Number(row.credit)
  form.location = row.location || ''
  form.description = row.description || ''
  dialogVisible.value = true
}

async function save() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!form.categoryId || !form.termId) return
  saving.value = true
  try {
    const payload: ActivitySaveParams = {
      title: form.title,
      categoryId: form.categoryId,
      termId: form.termId,
      organizerId: form.organizerId,
      startTime: form.startTime,
      endTime: form.endTime,
      signupStart: form.signupStart,
      signupEnd: form.signupEnd,
      capacity: form.capacity,
      credit: form.credit,
      location: form.location,
      description: form.description
    }
    if (isEdit.value && currentId.value) {
      await updateActivityApi(currentId.value, payload)
      ElMessage.success('活动已更新')
    } else {
      await createActivityApi(payload)
      ElMessage.success('活动已创建')
    }
    dialogVisible.value = false
    await loadList()
  } finally {
    saving.value = false
  }
}

async function publish(id: number) {
  await publishActivityApi(id)
  ElMessage.success('发布成功')
  await loadList()
}

async function finish(id: number) {
  await finishActivityApi(id)
  ElMessage.success('活动已完结')
  await loadList()
}

onMounted(async () => {
  await Promise.all([loadOptions(), loadList()])
})
</script>
