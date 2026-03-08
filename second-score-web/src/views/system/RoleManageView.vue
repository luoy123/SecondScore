<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">角色权限管理</h2>
      <el-button type="success" @click="openCreate">新增角色</el-button>
    </div>

    <el-table :data="roles" border stripe v-loading="loading">
      <el-table-column prop="roleCode" label="角色编码" width="180" />
      <el-table-column prop="roleName" label="角色名称" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180">
        <template #default="scope">
          <el-button type="primary" link @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="warning" link @click="openPermission(scope.row)">配置权限</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="roleDialog" :title="editingRoleId ? '编辑角色' : '新增角色'" width="520px" destroy-on-close>
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="95px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" :disabled="!!editingRoleId" placeholder="如 COUNSELOR" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingRole" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permissionDialog" title="配置角色权限" width="760px" destroy-on-close>
      <el-alert type="info" :closable="false" style="margin-bottom: 12px">
        已选 {{ selectedPermissionIds.length }} 项权限
      </el-alert>

      <div class="permission-group">
        <h4>菜单权限</h4>
        <el-checkbox-group v-model="selectedPermissionIds">
          <el-checkbox v-for="p in menuPermissions" :key="p.id" :value="p.id">
            {{ p.permName }}（{{ p.permCode }}）
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <div class="permission-group">
        <h4>接口权限</h4>
        <el-checkbox-group v-model="selectedPermissionIds">
          <el-checkbox v-for="p in apiPermissions" :key="p.id" :value="p.id">
            {{ p.permName }}（{{ p.permCode }}）
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <template #footer>
        <el-button @click="permissionDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingPerm" @click="savePermissions">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import {
  assignRolePermissionsApi,
  createRoleApi,
  listPermissionsApi,
  listRoleManageApi,
  listRolePermissionIdsApi,
  updateRoleApi,
  type PermissionItem,
  type RoleManageItem
} from '@/api/rbac'

const loading = ref(false)
const savingRole = ref(false)
const savingPerm = ref(false)

const roles = ref<RoleManageItem[]>([])
const permissions = ref<PermissionItem[]>([])

const roleDialog = ref(false)
const permissionDialog = ref(false)
const editingRoleId = ref<number>()
const permissionRoleId = ref<number>()

const selectedPermissionIds = ref<number[]>([])
const roleFormRef = ref<FormInstance>()

const roleForm = reactive({
  roleCode: '',
  roleName: '',
  status: 1
})

const roleRules: FormRules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z][A-Z0-9_]{1,30}$/, message: '仅支持大写字母/数字/下划线，且以字母开头', trigger: 'blur' }
  ],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const menuPermissions = computed(() => permissions.value.filter((p) => p.permType === 'MENU' && p.status === 1))
const apiPermissions = computed(() => permissions.value.filter((p) => p.permType === 'API' && p.status === 1))

function resetRoleForm() {
  roleForm.roleCode = ''
  roleForm.roleName = ''
  roleForm.status = 1
}

async function loadData() {
  loading.value = true
  try {
    const [roleData, permissionData] = await Promise.all([listRoleManageApi(), listPermissionsApi()])
    roles.value = roleData
    permissions.value = permissionData
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingRoleId.value = undefined
  resetRoleForm()
  roleDialog.value = true
}

function openEdit(row: RoleManageItem) {
  editingRoleId.value = row.id
  roleForm.roleCode = row.roleCode
  roleForm.roleName = row.roleName
  roleForm.status = row.status
  roleDialog.value = true
}

async function saveRole() {
  const valid = await roleFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingRole.value = true
  try {
    if (editingRoleId.value) {
      await updateRoleApi(editingRoleId.value, { ...roleForm })
      ElMessage.success('角色已更新')
    } else {
      await createRoleApi({ ...roleForm })
      ElMessage.success('角色已创建')
    }
    roleDialog.value = false
    await loadData()
  } finally {
    savingRole.value = false
  }
}

async function openPermission(row: RoleManageItem) {
  permissionRoleId.value = row.id
  selectedPermissionIds.value = await listRolePermissionIdsApi(row.id)
  permissionDialog.value = true
}

async function savePermissions() {
  if (!permissionRoleId.value) return
  savingPerm.value = true
  try {
    await assignRolePermissionsApi(permissionRoleId.value, selectedPermissionIds.value)
    ElMessage.success('权限配置成功')
    permissionDialog.value = false
  } finally {
    savingPerm.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.permission-group {
  margin-bottom: 14px;
  padding: 10px;
  border: 1px solid #e8ecf3;
  border-radius: 10px;
}

.permission-group h4 {
  margin: 0 0 10px;
}

.el-checkbox-group {
  display: grid;
  gap: 8px;
}
</style>
