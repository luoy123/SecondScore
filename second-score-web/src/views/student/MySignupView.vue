<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">我的报名</h2>
      <el-button @click="loadData">刷新</el-button>
    </div>

    <div class="page-card query-toolbar">
      <el-input v-model="query.keyword" clearable placeholder="活动名称/学号" style="width: 240px" />
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

    <el-table :data="filteredList" border stripe v-loading="loading">
      <el-table-column label="活动" min-width="180">
        <template #default="scope">{{ activityTitle(scope.row.activityId) }}</template>
      </el-table-column>
      <el-table-column prop="activityId" label="活动ID" width="90" />
      <el-table-column prop="applyTime" label="报名时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.applyTime) }}</template>
      </el-table-column>
      <el-table-column prop="reviewStatus" label="审核状态" width="120">
        <template #default="scope">
          <el-tag :type="tagType(scope.row.reviewStatus)">{{ scope.row.reviewStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="signupStatus" label="报名状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.signupStatus === 'ACTIVE' ? 'success' : 'info'">{{ scope.row.signupStatus }}</el-tag>
        </template>
      </el-table-column>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { cancelMySignupApi, listMySignupsApi, type SignupItem } from '@/api/signup'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const list = ref<SignupItem[]>([])
const activities = ref<ActivityItem[]>([])

const query = reactive({
  keyword: '',
  reviewStatus: '',
  signupStatus: ''
})

const activityMap = computed(() => {
  const map = new Map<number, string>()
  for (const item of activities.value) {
    map.set(item.id, item.title)
  }
  return map
})

const filteredList = computed(() =>
  list.value.filter((s) => {
    const keyword = query.keyword.trim().toLowerCase()
    const hitKeyword = !keyword
      || activityTitle(s.activityId).toLowerCase().includes(keyword)
      || String(s.activityId).includes(keyword)
      || (s.studentNo || '').toLowerCase().includes(keyword)
    const hitReview = !query.reviewStatus || s.reviewStatus === query.reviewStatus
    const hitSignup = !query.signupStatus || s.signupStatus === query.signupStatus
    return hitKeyword && hitReview && hitSignup
  })
)

function activityTitle(activityId: number) {
  return activityMap.value.get(activityId) || `活动#${activityId}`
}

function resetQuery() {
  query.keyword = ''
  query.reviewStatus = ''
  query.signupStatus = ''
}

function tagType(status: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    const [signupData, activityData] = await Promise.all([
      listMySignupsApi(),
      listActivitiesApi()
    ])
    list.value = signupData
    activities.value = activityData
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
