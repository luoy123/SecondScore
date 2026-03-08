<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">我的报名</h2>
      <el-button @click="loadData">刷新</el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="activityId" label="活动ID" width="90" />
      <el-table-column prop="applyTime" label="报名时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.applyTime) }}</template>
      </el-table-column>
      <el-table-column prop="reviewStatus" label="审核状态" width="120">
        <template #default="scope">
          <el-tag :type="tagType(scope.row.reviewStatus)">{{ scope.row.reviewStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="signupStatus" label="报名状态" width="120" />
      <el-table-column prop="reviewerName" label="审核人" width="120" />
      <el-table-column prop="reviewTime" label="审核时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.reviewTime) }}</template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="拒绝原因" min-width="160" />
      <el-table-column label="操作" fixed="right" width="120">
        <template #default="scope">
          <el-button
            type="danger"
            link
            :disabled="scope.row.signupStatus !== 'ACTIVE'"
            @click="cancelSignup(scope.row.activityId)"
          >取消报名</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

import { cancelMySignupApi, listMySignupsApi, type SignupItem } from '@/api/signup'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const list = ref<SignupItem[]>([])

function tagType(status: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    list.value = await listMySignupsApi()
  } finally {
    loading.value = false
  }
}

async function cancelSignup(activityId: number) {
  await ElMessageBox.confirm('确认取消该活动报名？', '提示', { type: 'warning' })
  await cancelMySignupApi(activityId)
  ElMessage.success('已取消报名')
  await loadData()
}

onMounted(loadData)
</script>
