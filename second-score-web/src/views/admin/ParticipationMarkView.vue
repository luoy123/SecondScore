<template>
  <div class="page-shell">
    <div class="page-title">
      <h2 class="brand-font">参与登记</h2>
      <el-space>
        <el-select v-model="activityId" filterable clearable placeholder="请选择活动" style="width: 320px" @change="loadData">
          <el-option v-for="a in activities" :key="a.id" :label="`${a.title}（${activityStatusLabel(a.status)}）`" :value="a.id" />
        </el-select>
        <el-button @click="loadData" :disabled="!activityId">刷新</el-button>
        <el-button type="primary" :disabled="!activityId || rows.length === 0" @click="submitMarks">提交登记</el-button>
      </el-space>
    </div>

    <el-empty v-if="!activityId" description="请先选择活动"></el-empty>

    <template v-else>
      <div class="page-card query-toolbar">
        <el-input v-model="query.keyword" clearable placeholder="姓名/学号" style="width: 220px" />
        <el-select v-model="query.attendanceStatus" clearable placeholder="参与状态" style="width: 150px">
          <el-option label="参加" value="PRESENT" />
          <el-option label="缺席" value="ABSENT" />
        </el-select>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <div class="stats-line">
        <el-tag type="success">参加 {{ presentCount }}</el-tag>
        <el-tag type="info">缺席 {{ absentCount }}</el-tag>
      </div>

      <el-table :data="filteredRows" border stripe v-loading="loading">
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column label="参与状态" width="180">
          <template #default="scope">
            <el-select v-model="scope.row.attendanceStatus" style="width: 150px">
              <el-option label="参加" value="PRESENT" />
              <el-option label="缺席" value="ABSENT" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="180">
          <template #default="scope">
            <el-input v-model="scope.row.remark" placeholder="可选" />
          </template>
        </el-table-column>
        <el-table-column label="上次登记" width="210">
          <template #default="scope">
            {{ scope.row.prevMarked ? `${scope.row.prevMarked} / ${scope.row.prevTime || '-'}` : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { listActivitiesApi, type ActivityItem } from '@/api/activity'
import { listActivitySignupsApi, listParticipationApi, markParticipationApi } from '@/api/signup'
import { activityStatusLabel } from '@/utils/enumLabel'
import { formatDateTime } from '@/utils/format'

interface MarkRow {
  studentId: number
  studentNo?: string
  studentName?: string
  attendanceStatus: 'PRESENT' | 'ABSENT'
  remark?: string
  prevMarked?: string
  prevTime?: string
}

const activities = ref<ActivityItem[]>([])
const activityId = ref<number>()
const rows = ref<MarkRow[]>([])
const loading = ref(false)

const query = reactive({
  keyword: '',
  attendanceStatus: ''
})

const filteredRows = computed(() =>
  rows.value.filter((r) => {
    const keyword = query.keyword.trim().toLowerCase()
    const hitKeyword = !keyword
      || (r.studentName || '').toLowerCase().includes(keyword)
      || (r.studentNo || '').toLowerCase().includes(keyword)
    const hitAttendance = !query.attendanceStatus || r.attendanceStatus === query.attendanceStatus
    return hitKeyword && hitAttendance
  })
)

const presentCount = computed(() => filteredRows.value.filter((r) => r.attendanceStatus === 'PRESENT').length)
const absentCount = computed(() => filteredRows.value.filter((r) => r.attendanceStatus === 'ABSENT').length)

function resetQuery() {
  query.keyword = ''
  query.attendanceStatus = ''
}

async function loadActivities() {
  activities.value = await listActivitiesApi()
}

async function loadData() {
  if (!activityId.value) return
  loading.value = true
  try {
    const [signups, participations] = await Promise.all([
      listActivitySignupsApi(activityId.value),
      listParticipationApi(activityId.value)
    ])

    const approved = signups.filter((s) => s.reviewStatus === 'APPROVED' && s.signupStatus === 'ACTIVE')
    const map = new Map(participations.map((p) => [p.studentId, p]))

    rows.value = approved.map((s) => {
      const old = map.get(s.studentId)
      return {
        studentId: s.studentId,
        studentNo: s.studentNo,
        studentName: s.studentName,
        attendanceStatus: (old?.attendanceStatus || 'PRESENT') as 'PRESENT' | 'ABSENT',
        remark: old?.remark || '',
        prevMarked: old?.markerName,
        prevTime: formatDateTime(old?.markedTime)
      }
    })
  } finally {
    loading.value = false
  }
}

async function submitMarks() {
  if (!activityId.value) return
  await markParticipationApi(activityId.value, rows.value.map((r) => ({
    studentId: r.studentId,
    attendanceStatus: r.attendanceStatus,
    remark: r.remark
  })))
  ElMessage.success('参与登记已提交')
  await loadData()
}

onMounted(loadActivities)
</script>

<style scoped>
.stats-line {
  margin: 10px 0;
  display: flex;
  gap: 8px;
}
</style>
