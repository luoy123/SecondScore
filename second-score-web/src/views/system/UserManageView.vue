<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">用户管理</h2>
      <el-space>
        <el-input v-model="keyword" clearable placeholder="用户名/姓名/学号" style="width: 240px" />
        <el-button type="primary" @click="loadUsers">查询</el-button>
        <el-button type="success" @click="openCreate">新增用户</el-button>
      </el-space>
    </div>

    <el-table :data="users" border stripe v-loading="loading">
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="user-cell">
            <el-avatar :src="row.avatarUrl" :size="34">{{ row.realName?.slice(-2) || row.username?.slice(0, 2) }}</el-avatar>
            <div class="user-meta">
              <strong>{{ row.realName }}</strong>
              <span>@{{ row.username }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="studentNo" label="学号" width="130" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column label="角色" min-width="160">
        <template #default="scope">
          <el-tag v-for="r in scope.row.roles" :key="r" style="margin-right: 4px">{{ r }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-switch
            :model-value="scope.row.status === 1"
            @change="(v:boolean) => changeStatus(scope.row.id, v ? 1 : 0)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="warning" link @click="openRole(scope.row)">分配角色</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="userDialog" :title="isCreate ? '新增用户' : '编辑用户'" width="720px" destroy-on-close>
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="95px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="用户名" prop="username"><el-input v-model="userForm.username" :disabled="!isCreate" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名" prop="realName"><el-input v-model="userForm.realName" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12" v-if="isCreate">
          <el-col :span="12"><el-form-item label="密码" prop="password"><el-input v-model="userForm.password" show-password /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学号"><el-input v-model="userForm.studentNo" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12" v-else>
          <el-col :span="12"><el-form-item label="学号"><el-input v-model="userForm.studentNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="userForm.phone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="userForm.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="学院"><el-select v-model="userForm.collegeId" clearable style="width: 100%" @change="onCollegeChange"><el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="专业"><el-select v-model="userForm.majorId" clearable style="width: 100%" @change="onMajorChange"><el-option v-for="m in majorOptions" :key="m.id" :label="m.majorName" :value="m.id" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="班级"><el-select v-model="userForm.classId" clearable style="width: 100%"><el-option v-for="c in classOptions" :key="c.id" :label="c.className" :value="c.id" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="userDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialog" title="分配角色" width="460px" destroy-on-close>
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="r in roles" :key="r.id" :value="r.id">{{ r.roleName }}（{{ r.roleCode }}）</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRoles">保存角色</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { listRolesApi, listUsersApi, createUserApi, updateUserApi, updateUserStatusApi, assignUserRolesApi, type RoleItem, type UserItem } from '@/api/user'
import { listClassesApi, listCollegesApi, listMajorsApi, type ClassItem, type CollegeItem, type MajorItem } from '@/api/base'

const loading = ref(false)
const saving = ref(false)
const users = ref<UserItem[]>([])
const roles = ref<RoleItem[]>([])

const keyword = ref('')

const colleges = ref<CollegeItem[]>([])
const majors = ref<MajorItem[]>([])
const classes = ref<ClassItem[]>([])

const majorOptions = computed(() => majors.value.filter((m) => !userForm.collegeId || m.collegeId === userForm.collegeId))
const classOptions = computed(() => classes.value.filter((c) => !userForm.majorId || c.majorId === userForm.majorId))

const userDialog = ref(false)
const roleDialog = ref(false)
const isCreate = ref(true)
const editingUserId = ref<number>()
const roleUserId = ref<number>()
const selectedRoleIds = ref<number[]>([])
const userFormRef = ref<FormInstance>()

const userForm = reactive({
  username: '',
  password: '123456',
  realName: '',
  studentNo: '',
  phone: '',
  email: '',
  collegeId: undefined as number | undefined,
  majorId: undefined as number | undefined,
  classId: undefined as number | undefined
})

const userRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function resetUserForm() {
  userForm.username = ''
  userForm.password = '123456'
  userForm.realName = ''
  userForm.studentNo = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.collegeId = undefined
  userForm.majorId = undefined
  userForm.classId = undefined
}

function onCollegeChange() {
  userForm.majorId = undefined
  userForm.classId = undefined
}

function onMajorChange() {
  userForm.classId = undefined
}

async function loadUsers() {
  loading.value = true
  try {
    users.value = await listUsersApi({ keyword: keyword.value || undefined })
  } finally {
    loading.value = false
  }
}

async function loadBase() {
  const [roleData, collegeData, majorData, classData] = await Promise.all([
    listRolesApi(),
    listCollegesApi(),
    listMajorsApi(),
    listClassesApi()
  ])
  roles.value = roleData
  colleges.value = collegeData
  majors.value = majorData
  classes.value = classData
}

function openCreate() {
  isCreate.value = true
  editingUserId.value = undefined
  resetUserForm()
  userDialog.value = true
}

function openEdit(row: UserItem) {
  isCreate.value = false
  editingUserId.value = row.id
  userForm.username = row.username
  userForm.realName = row.realName
  userForm.studentNo = row.studentNo || ''
  userForm.phone = row.phone || ''
  userForm.email = row.email || ''
  userForm.collegeId = row.collegeId
  userForm.majorId = row.majorId
  userForm.classId = row.classId
  userDialog.value = true
}

async function saveUser() {
  const valid = await userFormRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (isCreate.value) {
      await createUserApi({
        username: userForm.username,
        password: userForm.password,
        realName: userForm.realName,
        studentNo: userForm.studentNo || undefined,
        phone: userForm.phone || undefined,
        email: userForm.email || undefined,
        collegeId: userForm.collegeId,
        majorId: userForm.majorId,
        classId: userForm.classId
      })
      ElMessage.success('用户创建成功')
    } else if (editingUserId.value) {
      await updateUserApi(editingUserId.value, {
        realName: userForm.realName,
        phone: userForm.phone || undefined,
        email: userForm.email || undefined,
        collegeId: userForm.collegeId,
        majorId: userForm.majorId,
        classId: userForm.classId
      })
      ElMessage.success('用户更新成功')
    }
    userDialog.value = false
    await loadUsers()
  } finally {
    saving.value = false
  }
}

async function changeStatus(id: number, status: number) {
  await updateUserStatusApi(id, status)
  ElMessage.success('状态已更新')
  await loadUsers()
}

function openRole(row: UserItem) {
  roleUserId.value = row.id
  selectedRoleIds.value = roles.value.filter((r) => row.roles.includes(r.roleCode)).map((r) => r.id)
  roleDialog.value = true
}

async function saveRoles() {
  if (!roleUserId.value) return
  await assignUserRolesApi(roleUserId.value, selectedRoleIds.value)
  ElMessage.success('角色分配成功')
  roleDialog.value = false
  await loadUsers()
}

onMounted(async () => {
  await Promise.all([loadBase(), loadUsers()])
})
</script>

<style scoped>
.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-meta strong {
  font-size: 14px;
}

.user-meta span {
  font-size: 12px;
  color: var(--text-sub);
}
</style>
