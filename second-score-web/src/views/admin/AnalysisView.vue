<template>
  <div class="page-shell analysis-page">
    <div class="page-title">
      <h2 class="brand-font">数据分析</h2>
      <el-space>
        <el-select v-model="termId" clearable placeholder="选择学期" style="width: 200px" @change="loadCharts">
          <el-option v-for="t in terms" :key="t.id" :label="t.termName" :value="t.id" />
        </el-select>
        <el-select v-model="topN" placeholder="排行数量" style="width: 130px" @change="loadCharts">
          <el-option :value="5" label="Top 5" />
          <el-option :value="10" label="Top 10" />
          <el-option :value="20" label="Top 20" />
        </el-select>
        <el-switch v-model="includeZero" inline-prompt active-text="含零值" inactive-text="过滤零值" @change="loadCharts" />
        <el-button @click="loadAll">刷新数据</el-button>
      </el-space>
    </div>

    <div class="metric-grid">
      <div class="metric-card page-card">
        <span>活动总数</span>
        <strong>{{ metricDisplay.totalActivities }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>报名总数</span>
        <strong>{{ metricDisplay.totalSignups }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>发放学分总量</span>
        <strong>{{ metricDisplay.totalGrantedCredit }}</strong>
      </div>
      <div class="metric-card page-card">
        <span>获得学分人数</span>
        <strong>{{ metricDisplay.totalCreditedStudents }}</strong>
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
        <div class="chart-title">活动参与排行</div>
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
const topN = ref(10)
const includeZero = ref(true)
const dashboard = ref<DashboardData>({
  totalActivities: 0,
  totalSignups: 0,
  totalGrantedCredit: 0,
  totalCreditedStudents: 0
})
const metricDisplay = ref({
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
let metricAnimationFrame = 0

async function loadAll() {
  dashboard.value = await getDashboardApi()
  animateDashboard(dashboard.value)
  await loadCharts()
}

function animateDashboard(target: DashboardData) {
  const start = {
    totalActivities: Number(metricDisplay.value.totalActivities || 0),
    totalSignups: Number(metricDisplay.value.totalSignups || 0),
    totalGrantedCredit: Number(metricDisplay.value.totalGrantedCredit || 0),
    totalCreditedStudents: Number(metricDisplay.value.totalCreditedStudents || 0)
  }
  const end = {
    totalActivities: Number(target.totalActivities || 0),
    totalSignups: Number(target.totalSignups || 0),
    totalGrantedCredit: Number(target.totalGrantedCredit || 0),
    totalCreditedStudents: Number(target.totalCreditedStudents || 0)
  }
  const duration = 680
  const startAt = performance.now()
  cancelAnimationFrame(metricAnimationFrame)

  const tick = (now: number) => {
    const progress = Math.min((now - startAt) / duration, 1)
    const eased = 1 - (1 - progress) ** 4

    metricDisplay.value.totalActivities = Math.round(start.totalActivities + (end.totalActivities - start.totalActivities) * eased)
    metricDisplay.value.totalSignups = Math.round(start.totalSignups + (end.totalSignups - start.totalSignups) * eased)
    metricDisplay.value.totalGrantedCredit = Number(
      (start.totalGrantedCredit + (end.totalGrantedCredit - start.totalGrantedCredit) * eased).toFixed(2)
    )
    metricDisplay.value.totalCreditedStudents = Math.round(
      start.totalCreditedStudents + (end.totalCreditedStudents - start.totalCreditedStudents) * eased
    )

    if (progress < 1) {
      metricAnimationFrame = requestAnimationFrame(tick)
    }
  }

  metricAnimationFrame = requestAnimationFrame(tick)
}

function applyNoData(chart: echarts.ECharts, text: string) {
  chart.setOption({
    graphic: {
      type: 'text',
      left: 'center',
      top: 'middle',
      style: {
        text,
        fill: '#8b97ab',
        fontSize: 14
      }
    }
  })
}

async function loadCharts() {
  const [trend, category, ranking] = await Promise.all([
    getTermTrendApi(),
    getCategoryDistributionApi(termId.value),
    getActivityRankingApi({ termId: termId.value, topN: topN.value })
  ])

  const categoryData = includeZero.value ? category : category.filter((i) => Number(i.totalCredit) > 0)
  const rankingData = includeZero.value ? ranking : ranking.filter((i) => Number(i.participantCount) > 0)

  await nextTick()

  if (termTrendRef.value) {
    termTrendChart ??= echarts.init(termTrendRef.value)
    termTrendChart.setOption({
      color: ['#2f7cf6'],
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 18, top: 20, bottom: 36 },
      xAxis: { type: 'category', data: trend.map((i) => i.termName), axisTick: { alignWithLabel: true } },
      yAxis: { type: 'value', minInterval: 1, splitLine: { lineStyle: { color: '#e7edf5' } } },
      series: [{
        type: 'line',
        smooth: true,
        symbolSize: 8,
        areaStyle: { opacity: 0.12 },
        data: trend.map((i) => i.totalCredit)
      }],
      graphic: []
    })
  }

  if (categoryRef.value) {
    categoryChart ??= echarts.init(categoryRef.value)
    if (!categoryData.length) {
      categoryChart.clear()
      applyNoData(categoryChart, '暂无类别学分数据')
    } else {
      categoryChart.setOption({
        color: ['#2f7cf6', '#2db6a3', '#f6b93b', '#ff8a65', '#8c7ae6', '#00b894'],
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '44%'],
          label: { formatter: '{b}\n{c}' },
          data: categoryData.map((i) => ({ name: i.categoryName, value: i.totalCredit }))
        }],
        graphic: []
      })
    }
  }

  if (rankingRef.value) {
    rankingChart ??= echarts.init(rankingRef.value)
    if (!rankingData.length) {
      rankingChart.clear()
      applyNoData(rankingChart, '暂无活动参与数据')
    } else {
      rankingChart.setOption({
        color: ['#2db6a3'],
        tooltip: { trigger: 'axis' },
        grid: { left: 120, right: 20, top: 20, bottom: 20 },
        xAxis: { type: 'value', minInterval: 1, splitLine: { lineStyle: { color: '#ecf0f5' } } },
        yAxis: {
          type: 'category',
          data: rankingData.map((i) => i.activityTitle).reverse(),
          axisLabel: { width: 210, overflow: 'truncate' }
        },
        series: [{
          type: 'bar',
          barMaxWidth: 22,
          data: rankingData.map((i) => i.participantCount).reverse(),
          label: { show: true, position: 'right' }
        }],
        graphic: []
      })
    }
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
  cancelAnimationFrame(metricAnimationFrame)
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
  animation: card-rise 0.54s var(--ease-out-quint, cubic-bezier(0.22, 1, 0.36, 1)) both;
}

.metric-card:nth-child(2) {
  animation-delay: 0.06s;
}

.metric-card:nth-child(3) {
  animation-delay: 0.12s;
}

.metric-card:nth-child(4) {
  animation-delay: 0.18s;
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

@keyframes card-rise {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
