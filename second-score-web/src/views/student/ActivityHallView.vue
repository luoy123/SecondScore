<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">活动大厅</h2>
      <el-space>
        <el-select v-model="query.termId" clearable placeholder="按学期筛选" style="width: 180px" @change="loadActivities">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-input v-model="query.title" placeholder="搜索活动标题" clearable style="width: 220px" @keyup.enter="loadActivities" />
        <el-button type="primary" @click="loadActivities">查询</el-button>
      </el-space>
    </div>

    <div class="tip-text" style="margin-bottom: 10px">仅展示可参与的活动，点击“我要报名”即可提交。</div>

    <el-table :data="activities" border stripe v-loading="loading">
      <el-table-column prop="title" label="活动名称" min-width="190" />
      <el-table-column prop="categoryName" label="类别" width="120" />
      <el-table-column prop="termName" label="学期" width="140" />
      <el-table-column label="活动时间" min-width="170">
        <template #default="scope">
          {{ formatDateTime(scope.row.startTime) }} ~ {{ formatDateTime(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column label="报名时间" min-width="170">
        <template #default="scope">
          {{ formatDateTime(scope.row.signupStart) }} ~ {{ formatDateTime(scope.row.signupEnd) }}
        </template>
      </el-table-column>
      <el-table-column label="名额" width="130">
        <template #default="scope">
          {{ scope.row.signupCount || 0 }}/{{ scope.row.capacity }}
        </template>
      </el-table-column>
      <el-table-column prop="credit" label="学分" width="90" />
      <el-table-column prop="organizerName" label="负责人" width="120" />
      <el-table-column label="操作" fixed="right" width="120">
        <template #default="scope">
          <el-button
            type="primary"
            link
            :disabled="scope.row.status !== 'PUBLISHED'"
            @click="onSignup(scope.row.id)"
          >我要报名</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { listTermsApi, type TermItem } from '@/api/base'
import { signupActivityApi } from '@/api/signup'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const activities = ref<ActivityItem[]>([])
const terms = ref<TermItem[]>([])

const query = reactive({
  title: '',
  termId: undefined as number | undefined
})

async function loadActivities() {
  loading.value = true
  try {
    activities.value = await listActivitiesApi({ ...query, status: 'PUBLISHED' })
  } finally {
    loading.value = false
  }
}

async function onSignup(activityId: number) {
  await signupActivityApi(activityId)
  ElMessage.success('报名成功')
  await loadActivities()
}

async function loadTerms() {
  terms.value = await listTermsApi()
}

onMounted(async () => {
  await Promise.all([loadTerms(), loadActivities()])
})
</script>
