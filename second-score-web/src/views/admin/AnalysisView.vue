<template>
  <div class="page-shell analysis-page">
    <div class="page-title">
      <h2 class="brand-font">数据分析</h2>
      <el-space>
        <el-select v-model="termId" clearable placeholder="选择学期" style="width: 200px" @change="loadCharts">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-button @click="loadAll">刷新数据</el-button>
      </el-space>
    </div>

    <div class="metric-grid">
      <div class="metric-card page-card">
        <span>活动总数</span>
        <strong>{{ dashboard.totalActivities || 0 }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>报名总数</span>
        <strong>{{ dashboard.totalSignups || 0 }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>发放学分总量</span>
        <strong>{{ dashboard.totalGrantedCredit || 0 }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>获得学分人数</span>
        <strong>{{ dashboard.totalCreditedStudents || 0 }}</strong>
      </div>
    </div>

    <div class="chart-grid">
      <div class="chart-card page-card">
        <div class="chart-title">学期学分趋势</div>
        <div ref="termTrendRef" class="chart"></div>
      </div>
      <div class="chart-card page-card">
        <div class="chart-title">类别学分分布</div>
        <div ref="categoryRef" class="chart"></div>
      </div>
      <div class="chart-card page-card full">
        <div class="chart-title">活动参与排行 Top10</div>
        <div ref="rankingRef" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'

import { getActivityRankingApi, getCategoryDistributionApi, getDashboardApi, getTermTrendApi, type DashboardData } from '@/api/analysis'
import { listTermsApi, type TermItem } from '@/api/base'

const termId = ref<number>()
const terms = ref<TermItem[]>([])
const dashboard = ref<DashboardData>({
  totalActivities: 0,
  totalSignups: 0,
  totalGrantedCredit: 0,
  totalCreditedStudents: 0
})

const termTrendRef = ref<HTMLDivElement>()
const categoryRef = ref<HTMLDivElement>()
const rankingRef = ref<HTMLDivElement>()

let termTrendChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null
let rankingChart: echarts.ECharts | null = null

async function loadAll() {
  dashboard.value = await getDashboardApi()
  await loadCharts()
}

async function loadCharts() {
  const [trend, category, ranking] = await Promise.all([
    getTermTrendApi(),
    getCategoryDistributionApi(termId.value),
    getActivityRankingApi({ termId: termId.value, topN: 10 })
  ])

  await nextTick()

  if (termTrendRef.value) {
    termTrendChart ??= echarts.init(termTrendRef.value)
    termTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trend.map((i) => i.termName) },
      yAxis: { type: 'value' },
      series: [{
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        data: trend.map((i) => i.totalCredit)
      }]
    })
  }

  if (categoryRef.value) {
    categoryChart ??= echarts.init(categoryRef.value)
    categoryChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['42%', '72%'],
        data: category.map((i) => ({ name: i.categoryName, value: i.totalCredit }))
      }]
    })
  }

  if (rankingRef.value) {
    rankingChart ??= echarts.init(rankingRef.value)
    rankingChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'value' },
      yAxis: {
        type: 'category',
        data: ranking.map((i) => i.activityTitle).reverse()
      },
      series: [{
        type: 'bar',
        data: ranking.map((i) => i.participantCount).reverse(),
        label: { show: true, position: 'right' }
      }]
    })
  }
}

function resizeCharts() {
  termTrendChart?.resize()
  categoryChart?.resize()
  rankingChart?.resize()
}

onMounted(async () => {
  terms.value = await listTermsApi()
  await loadAll()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  termTrendChart?.dispose()
  categoryChart?.dispose()
  rankingChart?.dispose()
})
</script>

<style scoped>
.metric-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-bottom: 12px;
}

.metric-card {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.metric-card span {
  color: var(--text-sub);
  font-size: 13px;
}

.metric-card strong {
  font-size: clamp(24px, 3vw, 38px);
  color: var(--brand);
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.chart-card {
  padding: 12px;
}

.chart-card.full {
  grid-column: span 2;
}

.chart-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.chart {
  height: 340px;
}

@media (max-width: 1100px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }

  .chart-card.full {
    grid-column: span 1;
  }
}
</style>
