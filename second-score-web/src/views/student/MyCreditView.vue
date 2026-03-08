<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">我的学分</h2>
      <el-space>
        <el-select v-model="termId" clearable placeholder="按学期筛选" style="width: 180px" @change="loadAll">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-button @click="loadAll">刷新</el-button>
      </el-space>
    </div>

    <div class="summary page-card" style="padding: 14px; margin-bottom: 14px">
      <div class="summary-title">学分汇总</div>
      <div class="summary-value brand-font">{{ summary.totalCredit || 0 }}</div>
      <div class="tip-text">{{ summary.termName || '全部学期' }} · {{ summary.studentName || '-' }}</div>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="activityTitle" label="活动" min-width="170" />
      <el-table-column prop="categoryName" label="类别" width="120" />
      <el-table-column prop="termName" label="学期" width="130" />
      <el-table-column prop="credit" label="学分" width="90" />
      <el-table-column prop="grantStatus" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.grantStatus === 'GRANTED' ? 'success' : 'info'">{{ scope.row.grantStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="grantTime" label="发放时间" width="170">
        <template #default="scope">{{ formatDateTime(scope.row.grantTime) }}</template>
      </el-table-column>
      <el-table-column prop="revokeReason" label="撤销原因" min-width="140" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { getPersonalCreditSummaryApi, listCreditRecordsApi, type CreditRecordItem, type PersonalCreditSummary } from '@/api/credit'
import { listTermsApi, type TermItem } from '@/api/base'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const termId = ref<number>()
const terms = ref<TermItem[]>([])
const records = ref<CreditRecordItem[]>([])
const summary = ref<PersonalCreditSummary>({ studentId: 0, totalCredit: 0 })

async function loadAll() {
  loading.value = true
  try {
    const [recordData, summaryData] = await Promise.all([
      listCreditRecordsApi({ termId: termId.value }),
      getPersonalCreditSummaryApi({ termId: termId.value })
    ])
    records.value = recordData
    summary.value = summaryData
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  terms.value = await listTermsApi()
  await loadAll()
})
</script>

<style scoped>
.summary-title {
  color: var(--text-sub);
  font-size: 13px;
}

.summary-value {
  font-size: clamp(28px, 4vw, 42px);
  color: var(--brand);
  margin: 4px 0;
}
</style>
