<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">报名审核</h2>
      <el-space>
        <el-select v-model="activityId" filterable clearable placeholder="请选择活动" style="width: 320px" @change="loadSignups">
          <el-option v-for="a in activities" :key="a.id" :label="`${a.title} (${a.status})`" :value="a.id" />
        </el-select>
        <el-button @click="loadSignups" :disabled="!activityId">刷新</el-button>
      </el-space>
    </div>

    <el-empty v-if="!activityId" description="请先选择活动"></el-empty>

    <template v-else>
      <div class="page-card query-toolbar">
        <el-input v-model="query.keyword" clearable placeholder="姓名/学号" style="width: 220px" />
        <el-select v-model="query.reviewStatus" clearable placeholder="审核状态" style="width: 150px">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-select v-model="query.signupStatus" clearable placeholder="报名状态" style="width: 150px">
          <el-option label="有效报名" value="ACTIVE" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <div class="stats-line">
        <el-tag type="warning">待审核 {{ pendingCount }}</el-tag>
        <el-tag type="success">已通过 {{ approvedCount }}</el-tag>
        <el-tag type="danger">已拒绝 {{ rejectedCount }}</el-tag>
      </div>

      <el-table :data="filteredSignups" border stripe v-loading="loading">
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
        <el-table-column prop="signupStatus" label="报名状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.signupStatus === 'ACTIVE' ? 'success' : 'info'">{{ scope.row.signupStatus }}</el-tag>
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
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { approveSignupApi, listActivitySignupsApi, rejectSignupApi, type SignupItem } from '@/api/signup'
import { formatDateTime } from '@/utils/format'

const activities = ref<ActivityItem[]>([])
const activityId = ref<number>()
const signups = ref<SignupItem[]>([])
const loading = ref(false)

const query = reactive({
  keyword: '',
  reviewStatus: '',
  signupStatus: ''
})

const filteredSignups = computed(() =>
  signups.value.filter((s) => {
    const keyword = query.keyword.trim().toLowerCase()
    const hitKeyword = !keyword
      || (s.studentName || '').toLowerCase().includes(keyword)
      || (s.studentNo || '').toLowerCase().includes(keyword)
    const hitReview = !query.reviewStatus || s.reviewStatus === query.reviewStatus
    const hitSignup = !query.signupStatus || s.signupStatus === query.signupStatus
    return hitKeyword && hitReview && hitSignup
  })
)

const pendingCount = computed(() => filteredSignups.value.filter((s) => s.reviewStatus === 'PENDING').length)
const approvedCount = computed(() => filteredSignups.value.filter((s) => s.reviewStatus === 'APPROVED').length)
const rejectedCount = computed(() => filteredSignups.value.filter((s) => s.reviewStatus === 'REJECTED').length)

function tagType(status: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

function resetQuery() {
  query.keyword = ''
  query.reviewStatus = ''
  query.signupStatus = ''
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

<style scoped>
.stats-line {
  margin: 10px 0;
  display: flex;
  gap: 8px;
}
</style>
