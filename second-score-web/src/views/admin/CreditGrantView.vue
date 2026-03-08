<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">学分发放</h2>
      <el-space>
        <el-select v-model="grantActivityId" filterable placeholder="选择活动后发放" style="width: 320px">
          <el-option v-for="a in activities" :key="a.id" :label="`${a.title} (${a.status})`" :value="a.id" />
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

    <div class="page-card" style="padding: 12px; margin-bottom: 12px">
      <el-space>
        <el-input-number v-model="query.studentId" :min="1" placeholder="学生ID" />
        <el-select v-model="query.termId" clearable placeholder="学期" style="width: 170px">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-button @click="loadRecords">查询记录</el-button>
      </el-space>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生" width="120" />
      <el-table-column prop="studentNo" label="学号" width="130" />
      <el-table-column prop="activityTitle" label="活动" min-width="170" />
      <el-table-column prop="categoryName" label="类别" width="120" />
      <el-table-column prop="termName" label="学期" width="130" />
      <el-table-column prop="credit" label="学分" width="90" />
      <el-table-column prop="grantStatus" label="状态" width="110" />
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { listTermsApi, type TermItem } from '@/api/base'
import { grantCreditByActivityApi, listCreditRecordsApi, revokeCreditApi, type CreditGrantResult, type CreditRecordItem } from '@/api/credit'
import { formatDateTime } from '@/utils/format'

const activities = ref<ActivityItem[]>([])
const terms = ref<TermItem[]>([])
const grantActivityId = ref<number>()
const grantResult = ref<CreditGrantResult>()

const query = reactive({
  studentId: undefined as number | undefined,
  termId: undefined as number | undefined
})

const loading = ref(false)
const records = ref<CreditRecordItem[]>([])

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
</style>
