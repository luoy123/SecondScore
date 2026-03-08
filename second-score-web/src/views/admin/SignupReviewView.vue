<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">报名审核</h2>
      <el-space>
        <el-select v-model="activityId" filterable placeholder="请选择活动" style="width: 320px" @change="loadSignups">
          <el-option v-for="a in activities" :key="a.id" :label="`${a.title} (${a.status})`" :value="a.id" />
        </el-select>
        <el-button @click="loadSignups" :disabled="!activityId">刷新</el-button>
      </el-space>
    </div>

    <el-empty v-if="!activityId" description="请先选择活动"></el-empty>

    <el-table v-else :data="signups" border stripe v-loading="loading">
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="studentName" label="姓名" width="110" />
      <el-table-column prop="applyTime" label="报名时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.applyTime) }}</template>
      </el-table-column>
      <el-table-column prop="reviewStatus" label="审核状态" width="120">
        <template #default="scope">
          <el-tag :type="tagType(scope.row.reviewStatus)">{{ scope.row.reviewStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reviewerName" label="审核人" width="110" />
      <el-table-column prop="reviewTime" label="审核时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.reviewTime) }}</template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="拒绝原因" min-width="140" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button type="success" link :disabled="scope.row.reviewStatus !== 'PENDING'" @click="approve(scope.row.id)">通过</el-button>
          <el-button type="danger" link :disabled="scope.row.reviewStatus !== 'PENDING'" @click="reject(scope.row.id)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { approveSignupApi, listActivitySignupsApi, rejectSignupApi, type SignupItem } from '@/api/signup'
import { formatDateTime } from '@/utils/format'

const activities = ref<ActivityItem[]>([])
const activityId = ref<number>()
const signups = ref<SignupItem[]>([])
const loading = ref(false)

function tagType(status: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

async function loadActivities() {
  activities.value = await listActivitiesApi()
}

async function loadSignups() {
  if (!activityId.value) return
  loading.value = true
  try {
    signups.value = await listActivitySignupsApi(activityId.value)
  } finally {
    loading.value = false
  }
}

async function approve(id: number) {
  await approveSignupApi(id)
  ElMessage.success('已审核通过')
  await loadSignups()
}

async function reject(id: number) {
  const reason = await ElMessageBox.prompt('请输入拒绝原因', '拒绝报名', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '拒绝原因不能为空'
  }).then((res) => res.value)
  await rejectSignupApi(id, reason)
  ElMessage.success('已拒绝')
  await loadSignups()
}

onMounted(async () => {
  await loadActivities()
})
</script>
