<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" clearable />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="openAdd"
          v-hasPermi="['shebao:management:resume:add']"
        >添加</el-button>
      </el-col>
    </el-row>

    <el-table class="rx-table--compact" v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyTypes" min-width="180" />
      <el-table-column label="恢复补贴类型" prop="resumedSubsidyTypes" min-width="180" />
      <el-table-column label="恢复年月" width="120">
        <template slot-scope="scope">
          {{ formatMonthText(scope.row.resumeMonth) }}
        </template>
      </el-table-column>
      <el-table-column label="恢复原因" prop="resumeReason" min-width="180" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="添加待遇恢复" :visible.sync="addOpen" width="1100px" append-to-body>
      <el-form :inline="true" size="small" class="mb10">
        <el-form-item label="身份证号">
          <el-input v-model="candidateQueryIdCardNo" placeholder="请输入身份证号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchCandidate">搜索</el-button>
        </el-form-item>
      </el-form>

      <div v-if="candidate">
        <el-descriptions title="基本信息" :column="3" border size="small">
          <el-descriptions-item label="姓名">{{ candidate.name }}</el-descriptions-item>
          <el-descriptions-item label="街道">{{ candidate.streetOfficeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="村委会">{{ candidate.villageCommitteeName || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">待遇恢复</div>
        <el-table class="rx-table--compact" :data="editResumeItems" border size="small">
          <el-table-column label="补贴类型" min-width="130">
            <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
          </el-table-column>
          <el-table-column label="暂停年月" width="120">
            <template slot-scope="scope">{{ formatMonthText(scope.row.pauseStartMonth) }}</template>
          </el-table-column>
          <el-table-column label="是否需要恢复" width="130">
            <template slot-scope="scope">
              <el-select v-model="scope.row.needResume" size="mini">
                <el-option label="是" value="1" />
                <el-option label="否" value="0" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="恢复原因" min-width="160">
            <template slot-scope="scope">
              <el-input v-model="scope.row.resumeReason" size="mini" placeholder="请输入恢复原因" />
            </template>
          </el-table-column>
          <el-table-column label="恢复年月" width="140">
            <template slot-scope="scope">
              <el-date-picker
                v-model="scope.row.resumeMonth"
                type="month"
                value-format="yyyy-MM"
                size="mini"
                placeholder="恢复年月"
                style="width: 120px"
              />
            </template>
          </el-table-column>
          <el-table-column label="补发开始年月" width="130">
            <template slot-scope="scope">
              {{ calcSupplementInfo(scope.row).startMonth }}
            </template>
          </el-table-column>
          <el-table-column label="补发终止年月" width="130">
            <template slot-scope="scope">
              {{ calcSupplementInfo(scope.row).endMonth }}
            </template>
          </el-table-column>
          <el-table-column label="补发月数" width="100">
            <template slot-scope="scope">
              {{ calcSupplementInfo(scope.row).months }}
            </template>
          </el-table-column>
          <el-table-column label="补发金额" width="120">
            <template slot-scope="scope">
              {{ calcSupplementInfo(scope.row).amount }} 元
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="140">
            <template slot-scope="scope">
              <el-input v-model="scope.row.remark" size="mini" placeholder="备注" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer">
        <el-button type="primary" :loading="submitLoading" @click="submitResume">保 存</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="待遇恢复详情" :visible.sync="detailOpen" width="1100px" append-to-body>
      <div v-if="detailData.id">
        <el-descriptions title="基本信息" :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ detailData.idCardNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="街道">{{ detailData.streetOfficeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="村委会">{{ detailData.villageCommitteeName || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">待遇恢复</div>
        <el-table class="rx-table--compact" :data="detailData.resumeItems || []" border size="small">
          <el-table-column label="补贴类型" min-width="130">
            <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
          </el-table-column>
          <el-table-column label="暂停年月" width="120">
            <template slot-scope="scope">{{ formatMonthText(scope.row.pauseStartMonth) }}</template>
          </el-table-column>
          <el-table-column label="是否需要恢复" width="120">
            <template slot-scope="scope">{{ scope.row.needResume === '1' ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="恢复原因" prop="resumeReason" min-width="160" />
          <el-table-column label="恢复年月" width="120">
            <template slot-scope="scope">{{ formatMonthText(scope.row.resumeMonth) }}</template>
          </el-table-column>
          <el-table-column label="补发开始年月" width="130">
            <template slot-scope="scope">{{ formatMonthText(scope.row.supplementStartMonth) }}</template>
          </el-table-column>
          <el-table-column label="补发终止年月" width="130">
            <template slot-scope="scope">{{ formatMonthText(scope.row.supplementEndMonth) }}</template>
          </el-table-column>
          <el-table-column label="补发月数" prop="supplementMonths" width="100" />
          <el-table-column label="补发金额" width="120">
            <template slot-scope="scope">{{ money(scope.row.supplementAmount) }} 元</template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="140" />
        </el-table>
      </div>
      <div slot="footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addBenefitResume,
  getBenefitResumeDetail,
  getResumeCandidate,
  listBenefitResume
} from '@/api/shebao/benefitResume'

export default {
  name: 'BenefitResume',
  data() {
    return {
      loading: true,
      submitLoading: false,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, idCardNo: null },
      addOpen: false,
      detailOpen: false,
      candidateQueryIdCardNo: null,
      candidate: null,
      editResumeItems: [],
      detailData: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitResume(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    openAdd() {
      this.addOpen = true
      this.candidateQueryIdCardNo = null
      this.candidate = null
      this.editResumeItems = []
    },
    searchCandidate() {
      if (!this.candidateQueryIdCardNo) {
        this.$modal.msgWarning('请输入身份证号')
        return
      }
      getResumeCandidate(this.candidateQueryIdCardNo).then(response => {
        this.candidate = response.data
        this.editResumeItems = (this.candidate.resumeItems || []).map(item => ({
          determinationItemId: item.determinationItemId,
          subsidyType: item.subsidyType,
          pauseStartMonth: item.pauseStartMonth,
          needResume: item.needResume || '1',
          resumeReason: item.resumeReason || '',
          resumeMonth: this.formatMonthText(item.resumeMonth),
          remark: item.remark || '',
          subsidyStandard: item.subsidyStandard
        }))
      }).catch(() => {
        this.candidate = null
        this.editResumeItems = []
      })
    },
    submitResume() {
      if (!this.candidate) {
        this.$modal.msgWarning('请先按身份证搜索待遇人员')
        return
      }
      if (this.editResumeItems.length === 0) {
        this.$modal.msgWarning('没有可恢复的补贴')
        return
      }
      const invalid = this.editResumeItems.some(item => !item.resumeMonth)
      if (invalid) {
        this.$modal.msgWarning('请为每条记录选择恢复年月')
        return
      }
      const needRows = this.editResumeItems.filter(item => item.needResume === '1')
      if (needRows.length === 0) {
        this.$modal.msgWarning('请至少选择一条需要恢复的补贴')
        return
      }
      this.submitLoading = true
      addBenefitResume({
        determinationId: this.candidate.determinationId,
        subsidyPersonId: this.candidate.subsidyPersonId,
        idCardNo: this.candidate.idCardNo,
        items: this.editResumeItems.map(item => ({
          determinationItemId: item.determinationItemId,
          needResume: item.needResume,
          resumeReason: item.resumeReason,
          resumeMonth: item.resumeMonth,
          remark: item.remark
        }))
      }).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.addOpen = false
        this.getList()
      }).finally(() => {
        this.submitLoading = false
      })
    },
    openDetail(row) {
      getBenefitResumeDetail(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    calcSupplementInfo(row) {
      const resumeYm = this.parseYearMonth(row.resumeMonth)
      if (!resumeYm) {
        return { startMonth: '-', endMonth: '-', months: 0, amount: '0.00' }
      }
      const currentYm = this.currentYearMonth()
      const endYm = this.minusOneMonth(currentYm)
      const startText = this.ymText(resumeYm.year, resumeYm.month)
      if (this.compareYm(resumeYm, endYm) > 0) {
        return { startMonth: startText, endMonth: '-', months: 0, amount: '0.00' }
      }
      const months = this.monthDiffInclusive(resumeYm, endYm)
      const standard = Number(row.subsidyStandard || 0)
      return {
        startMonth: startText,
        endMonth: this.ymText(endYm.year, endYm.month),
        months,
        amount: (months * standard).toFixed(2)
      }
    },
    subsidyTypeLabel(value) {
      return {
        land_loss: '失地居民',
        land_loss_resident: '失地居民',
        '1': '失地居民',
        expropriatee: '被征地居民',
        expropriatee_subsidy: '被征地居民',
        '2': '被征地居民',
        demolition: '拆迁居民',
        demolition_resident: '拆迁居民',
        '3': '拆迁居民',
        village_official: '村干部',
        '4': '村干部',
        teacher: '教师',
        teacher_subsidy: '教师',
        '5': '教师'
      }[value] || value || '-'
    },
    formatMonthText(value) {
      if (!value) return '-'
      if (typeof value === 'string') return value.slice(0, 7)
      if (value instanceof Date) {
        return `${value.getFullYear()}-${String(value.getMonth() + 1).padStart(2, '0')}`
      }
      return '-'
    },
    currentYearMonth() {
      const now = new Date()
      return { year: now.getFullYear(), month: now.getMonth() + 1 }
    },
    parseYearMonth(text) {
      if (!text || !/^\d{4}-\d{2}$/.test(text)) return null
      const parts = text.split('-')
      return { year: Number(parts[0]), month: Number(parts[1]) }
    },
    compareYm(a, b) {
      if (a.year !== b.year) return a.year - b.year
      return a.month - b.month
    },
    minusOneMonth(ym) {
      let year = ym.year
      let month = ym.month - 1
      if (month <= 0) {
        year -= 1
        month = 12
      }
      return { year, month }
    },
    monthDiffInclusive(start, end) {
      const diff = (end.year - start.year) * 12 + (end.month - start.month)
      return diff >= 0 ? diff + 1 : 0
    },
    ymText(year, month) {
      return `${year}-${String(month).padStart(2, '0')}`
    },
    money(value) {
      const number = Number(value || 0)
      return Number.isNaN(number) ? '0.00' : number.toFixed(2)
    }
  }
}
</script>

<style scoped>
.section-title {
  margin: 16px 0 10px;
  font-size: 14px;
  font-weight: 600;
}

.mb8 {
  margin-bottom: 8px;
}

.mb10 {
  margin-bottom: 10px;
}
</style>
