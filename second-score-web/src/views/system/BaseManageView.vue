<template>
  <div class="page-shell base-manage">
    <div class="page-title">
      <h2 class="brand-font">基础信息管理</h2>
      <el-button @click="reloadAll">刷新全部</el-button>
    </div>

    <el-tabs v-model="tab">
      <el-tab-pane label="学院" name="college">
        <div class="action-row"><el-button type="primary" @click="openCollege()">新增学院</el-button></div>
        <el-table :data="colleges" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="collegeCode" label="编码" width="150" />
          <el-table-column prop="collegeName" label="名称" min-width="180" />
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope"><el-button type="primary" link @click="openCollege(scope.row)">编辑</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="专业/班级" name="majorClass">
        <div class="split-grid">
          <section>
            <div class="action-row"><strong>专业</strong><el-button type="primary" @click="openMajor()">新增专业</el-button></div>
            <el-table :data="majors" border stripe>
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="majorCode" label="编码" width="120" />
              <el-table-column prop="majorName" label="专业名称" min-width="140" />
              <el-table-column prop="collegeId" label="学院ID" width="90" />
              <el-table-column label="操作" width="90"><template #default="scope"><el-button type="primary" link @click="openMajor(scope.row)">编辑</el-button></template></el-table-column>
            </el-table>
          </section>
          <section>
            <div class="action-row"><strong>班级</strong><el-button type="primary" @click="openClass()">新增班级</el-button></div>
            <el-table :data="classes" border stripe>
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="className" label="班级名称" min-width="140" />
              <el-table-column prop="majorId" label="专业ID" width="90" />
              <el-table-column prop="grade" label="年级" width="90" />
              <el-table-column label="操作" width="90"><template #default="scope"><el-button type="primary" link @click="openClass(scope.row)">编辑</el-button></template></el-table-column>
            </el-table>
          </section>
        </div>
      </el-tab-pane>

      <el-tab-pane label="活动类别/学分规则" name="category">
        <div class="action-row"><el-button type="primary" @click="openCategory()">新增类别</el-button></div>
        <el-table :data="categories" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="categoryName" label="类别名称" min-width="140" />
          <el-table-column prop="defaultCredit" label="默认学分" width="110" />
          <el-table-column prop="maxCreditPerTerm" label="学期上限" width="120" />
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column label="操作" width="90"><template #default="scope"><el-button type="primary" link @click="openCategory(scope.row)">编辑</el-button></template></el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="学期" name="term">
        <div class="action-row"><el-button type="primary" @click="openTerm()">新增学期</el-button></div>
        <el-table :data="terms" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="termName" label="学期名称" min-width="140" />
          <el-table-column label="开始日期" width="120"><template #default="scope">{{ formatDate(scope.row.startDate) }}</template></el-table-column>
          <el-table-column label="结束日期" width="120"><template #default="scope">{{ formatDate(scope.row.endDate) }}</template></el-table-column>
          <el-table-column prop="isCurrent" label="当前学期" width="100" />
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column label="操作" width="90"><template #default="scope"><el-button type="primary" link @click="openTerm(scope.row)">编辑</el-button></template></el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dlg.college" :title="editing.college ? '编辑学院' : '新增学院'" width="460px">
      <el-form :model="forms.college" label-width="95px">
        <el-form-item label="学院编码"><el-input v-model="forms.college.collegeCode" /></el-form-item>
        <el-form-item label="学院名称"><el-input v-model="forms.college.collegeName" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="collegeStatusBool" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.college=false">取消</el-button><el-button type="primary" @click="saveCollege">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="dlg.major" :title="editing.major ? '编辑专业' : '新增专业'" width="500px">
      <el-form :model="forms.major" label-width="95px">
        <el-form-item label="学院"><el-select v-model="forms.major.collegeId" style="width:100%"><el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="专业编码"><el-input v-model="forms.major.majorCode" /></el-form-item>
        <el-form-item label="专业名称"><el-input v-model="forms.major.majorName" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="majorStatusBool" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.major=false">取消</el-button><el-button type="primary" @click="saveMajor">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="dlg.classInfo" :title="editing.classInfo ? '编辑班级' : '新增班级'" width="500px">
      <el-form :model="forms.classInfo" label-width="95px">
        <el-form-item label="专业"><el-select v-model="forms.classInfo.majorId" style="width:100%"><el-option v-for="m in majors" :key="m.id" :label="m.majorName" :value="m.id" /></el-select></el-form-item>
        <el-form-item label="班级名称"><el-input v-model="forms.classInfo.className" /></el-form-item>
        <el-form-item label="年级"><el-input-number v-model="forms.classInfo.grade" :min="2000" :max="2100" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="classStatusBool" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.classInfo=false">取消</el-button><el-button type="primary" @click="saveClass">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="dlg.category" :title="editing.category ? '编辑类别规则' : '新增类别规则'" width="500px">
      <el-form :model="forms.category" label-width="95px">
        <el-form-item label="类别名称"><el-input v-model="forms.category.categoryName" /></el-form-item>
        <el-form-item label="默认学分"><el-input-number v-model="forms.category.defaultCredit" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="学期上限"><el-input-number v-model="forms.category.maxCreditPerTerm" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="categoryStatusBool" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.category=false">取消</el-button><el-button type="primary" @click="saveCategory">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="dlg.term" :title="editing.term ? '编辑学期' : '新增学期'" width="520px">
      <el-form :model="forms.term" label-width="95px">
        <el-form-item label="学期名称"><el-input v-model="forms.term.termName" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="forms.term.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="forms.term.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="当前学期"><el-switch v-model="termCurrentBool" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="termStatusBool" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.term=false">取消</el-button><el-button type="primary" @click="saveTerm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import {
  createCategoryApi,
  createClassApi,
  createCollegeApi,
  createMajorApi,
  createTermApi,
  listCategoriesApi,
  listClassesApi,
  listCollegesApi,
  listMajorsApi,
  listTermsApi,
  updateCategoryApi,
  updateClassApi,
  updateCollegeApi,
  updateMajorApi,
  updateTermApi,
  type CategoryItem,
  type ClassItem,
  type CollegeItem,
  type MajorItem,
  type TermItem
} from '@/api/base'
import { formatDate } from '@/utils/format'

const tab = ref('college')

const colleges = ref<CollegeItem[]>([])
const majors = ref<MajorItem[]>([])
const classes = ref<ClassItem[]>([])
const categories = ref<CategoryItem[]>([])
const terms = ref<TermItem[]>([])

const dlg = reactive({ college: false, major: false, classInfo: false, category: false, term: false })
const editing = reactive({
  college: undefined as CollegeItem | undefined,
  major: undefined as MajorItem | undefined,
  classInfo: undefined as ClassItem | undefined,
  category: undefined as CategoryItem | undefined,
  term: undefined as TermItem | undefined
})

const forms = reactive({
  college: { collegeCode: '', collegeName: '', status: 1 },
  major: { collegeId: undefined as number | undefined, majorCode: '', majorName: '', status: 1 },
  classInfo: { majorId: undefined as number | undefined, className: '', grade: 2023, status: 1 },
  category: { categoryName: '', defaultCredit: 1, maxCreditPerTerm: 6, status: 1 },
  term: { termName: '', startDate: '', endDate: '', isCurrent: 0, status: 1 }
})

const collegeStatusBool = computed({ get: () => forms.college.status === 1, set: (v) => (forms.college.status = v ? 1 : 0) })
const majorStatusBool = computed({ get: () => forms.major.status === 1, set: (v) => (forms.major.status = v ? 1 : 0) })
const classStatusBool = computed({ get: () => forms.classInfo.status === 1, set: (v) => (forms.classInfo.status = v ? 1 : 0) })
const categoryStatusBool = computed({ get: () => forms.category.status === 1, set: (v) => (forms.category.status = v ? 1 : 0) })
const termCurrentBool = computed({ get: () => forms.term.isCurrent === 1, set: (v) => (forms.term.isCurrent = v ? 1 : 0) })
const termStatusBool = computed({ get: () => forms.term.status === 1, set: (v) => (forms.term.status = v ? 1 : 0) })

async function reloadAll() {
  const [a, b, c, d, e] = await Promise.all([listCollegesApi(), listMajorsApi(), listClassesApi(), listCategoriesApi(), listTermsApi()])
  colleges.value = a
  majors.value = b
  classes.value = c
  categories.value = d
  terms.value = e
}

function openCollege(row?: CollegeItem) {
  editing.college = row
  forms.college.collegeCode = row?.collegeCode || ''
  forms.college.collegeName = row?.collegeName || ''
  forms.college.status = row?.status ?? 1
  dlg.college = true
}

async function saveCollege() {
  if (editing.college) {
    await updateCollegeApi(editing.college.id, forms.college)
  } else {
    await createCollegeApi(forms.college)
  }
  ElMessage.success('学院信息已保存')
  dlg.college = false
  await reloadAll()
}

function openMajor(row?: MajorItem) {
  editing.major = row
  forms.major.collegeId = row?.collegeId
  forms.major.majorCode = row?.majorCode || ''
  forms.major.majorName = row?.majorName || ''
  forms.major.status = row?.status ?? 1
  dlg.major = true
}

async function saveMajor() {
  if (!forms.major.collegeId) return ElMessage.warning('请选择学院')
  if (editing.major) {
    await updateMajorApi(editing.major.id, forms.major as any)
  } else {
    await createMajorApi(forms.major as any)
  }
  ElMessage.success('专业信息已保存')
  dlg.major = false
  await reloadAll()
}

function openClass(row?: ClassItem) {
  editing.classInfo = row
  forms.classInfo.majorId = row?.majorId
  forms.classInfo.className = row?.className || ''
  forms.classInfo.grade = row?.grade || 2023
  forms.classInfo.status = row?.status ?? 1
  dlg.classInfo = true
}

async function saveClass() {
  if (!forms.classInfo.majorId) return ElMessage.warning('请选择专业')
  if (editing.classInfo) {
    await updateClassApi(editing.classInfo.id, forms.classInfo as any)
  } else {
    await createClassApi(forms.classInfo as any)
  }
  ElMessage.success('班级信息已保存')
  dlg.classInfo = false
  await reloadAll()
}

function openCategory(row?: CategoryItem) {
  editing.category = row
  forms.category.categoryName = row?.categoryName || ''
  forms.category.defaultCredit = Number(row?.defaultCredit ?? 1)
  forms.category.maxCreditPerTerm = Number(row?.maxCreditPerTerm ?? 6)
  forms.category.status = row?.status ?? 1
  dlg.category = true
}

async function saveCategory() {
  if (editing.category) {
    await updateCategoryApi(editing.category.id, forms.category)
  } else {
    await createCategoryApi(forms.category)
  }
  ElMessage.success('类别规则已保存')
  dlg.category = false
  await reloadAll()
}

function openTerm(row?: TermItem) {
  editing.term = row
  forms.term.termName = row?.termName || ''
  forms.term.startDate = row?.startDate || ''
  forms.term.endDate = row?.endDate || ''
  forms.term.isCurrent = row?.isCurrent ?? 0
  forms.term.status = row?.status ?? 1
  dlg.term = true
}

async function saveTerm() {
  if (editing.term) {
    await updateTermApi(editing.term.id, forms.term)
  } else {
    await createTermApi(forms.term)
  }
  ElMessage.success('学期信息已保存')
  dlg.term = false
  await reloadAll()
}

onMounted(reloadAll)
</script>

<style scoped>
.action-row {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.split-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

@media (max-width: 1100px) {
  .split-grid {
    grid-template-columns: 1fr;
  }
}
</style>
