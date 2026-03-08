<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">学分发放</h2>
      <el-space>
        <el-select v-model="grantActivityId" filterable placeholder="选择活动后发放" style="width: 320px">
          <el-option v-for="a in activities" :key="a.id" :label="`${a.title}（${activityStatusLabel(a.status)}）`" :value="a.id" />
        </el-select>
        <el-button type="primary" :disabled="!grantActivityId" @click="grant">一键发放</el-button>
      </el-space>
    </div>

    <el-alert v-if="grantResult" type="success" :closable="false" show-icon style="margin-bottom: 12px">
      <template #title>
        发放完成：成功 {{ grantResult.grantedCount }} 人，跳过 {{ grantResult.skippedCount }} 人
      </template>
      <template #default>
        <div class="grant-messages">
          <div v-for="(m, idx) in grantResult.messages" :key="idx">{{ m }}</div>
        </div>
      </template>
    </el-alert>

    <div class="page-card query-toolbar">
      <el-input-number v-model="query.studentId" :min="1" placeholder="学生ID" />
      <el-input v-model="query.keyword" clearable placeholder="姓名/学号/活动名称" style="width: 220px" />
      <el-select v-model="query.termId" clearable placeholder="学期" style="width: 170px">
        <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
      </el-select>
      <el-select v-model="query.grantStatus" clearable placeholder="状态" style="width: 140px">
        <el-option label="已发放" value="GRANTED" />
        <el-option label="已撤销" value="REVOKED" />
      </el-select>
      <el-select v-model="query.activityId" clearable filterable placeholder="活动筛选" style="width: 220px">
        <el-option v-for="a in activities" :key="a.id" :label="a.title" :value="a.id" />
      </el-select>
      <el-button type="primary" @click="loadRecords">查询记录</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <div class="stats-line">
      <el-tag type="success">已发放 {{ grantedCount }}</el-tag>
      <el-tag type="info">已撤销 {{ revokedCount }}</el-tag>
    </div>

    <el-table :data="filteredRecords" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生" width="120" />
      <el-table-column prop="studentNo" label="学号" width="130" />
      <el-table-column prop="activityTitle" label="活动" min-width="170" />
      <el-table-column prop="categoryName" label="类别" width="120" />
      <el-table-column prop="termName" label="学期" width="130" />
      <el-table-column prop="credit" label="学分" width="90" />
      <el-table-column prop="grantStatus" label="状态" width="110">
        <template #default="scope">
          <el-tag :type="scope.row.grantStatus === 'GRANTED' ? 'success' : 'info'">{{ grantStatusLabel(scope.row.grantStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="grantTime" label="发放时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.grantTime) }}</template>
      </el-table-column>
      <el-table-column prop="revokeReason" label="撤销原因" min-width="130" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="scope">
          <el-button type="danger" link :disabled="scope.row.grantStatus !== 'GRANTED'" @click="revoke(scope.row.id)">撤销</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { listTermsApi, type TermItem } from '@/api/base'
import { grantCreditByActivityApi, listCreditRecordsApi, revokeCreditApi, type CreditGrantResult, type CreditRecordItem } from '@/api/credit'
import { activityStatusLabel, grantStatusLabel } from '@/utils/enumLabel'
import { formatDateTime } from '@/utils/format'

const activities = ref<ActivityItem[]>([])
const terms = ref<TermItem[]>([])
const grantActivityId = ref<number>()
const grantResult = ref<CreditGrantResult>()

const query = reactive({
  studentId: undefined as number | undefined,
  keyword: '',
  termId: undefined as number | undefined,
  grantStatus: '',
  activityId: undefined as number | undefined
})

const loading = ref(false)
const records = ref<CreditRecordItem[]>([])

const filteredRecords = computed(() =>
  records.value.filter((r) => {
    const keyword = query.keyword.trim().toLowerCase()
    const hitKeyword = !keyword
      || (r.studentName || '').toLowerCase().includes(keyword)
      || (r.studentNo || '').toLowerCase().includes(keyword)
      || (r.activityTitle || '').toLowerCase().includes(keyword)
    const hitStatus = !query.grantStatus || r.grantStatus === query.grantStatus
    const hitActivity = !query.activityId || r.activityId === query.activityId
    return hitKeyword && hitStatus && hitActivity
  })
)

const grantedCount = computed(() => filteredRecords.value.filter((r) => r.grantStatus === 'GRANTED').length)
const revokedCount = computed(() => filteredRecords.value.filter((r) => r.grantStatus === 'REVOKED').length)

function resetQuery() {
  query.studentId = undefined
  query.keyword = ''
  query.termId = undefined
  query.grantStatus = ''
  query.activityId = undefined
  loadRecords()
}

async function loadBase() {
  const [activityData, termData] = await Promise.all([listActivitiesApi(), listTermsApi()])
  activities.value = activityData
  terms.value = termData
}

async function grant() {
  if (!grantActivityId.value) return
  grantResult.value = await grantCreditByActivityApi(grantActivityId.value)
  ElMessage.success('学分发放完成')
  await loadRecords()
}

async function loadRecords() {
  loading.value = true
  try {
    records.value = await listCreditRecordsApi({
      studentId: query.studentId,
      termId: query.termId
    })
  } finally {
    loading.value = false
  }
}

async function revoke(id: number) {
  const reason = await ElMessageBox.prompt('请输入撤销原因', '撤销学分', {
    inputPattern: /\S+/,
    inputErrorMessage: '原因不能为空',
    confirmButtonText: '确认撤销',
    cancelButtonText: '取消'
  }).then((res) => res.value)
  await revokeCreditApi(id, reason)
  ElMessage.success('学分已撤销')
  await loadRecords()
}

onMounted(async () => {
  await loadBase()
  await loadRecords()
})
</script>

<style scoped>
.grant-messages {
  max-height: 120px;
  overflow: auto;
  font-size: 13px;
  color: var(--text-sub);
}

.stats-line {
  margin: 10px 0;
  display: flex;
  gap: 8px;
}
</style>
