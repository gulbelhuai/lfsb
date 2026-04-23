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
          v-hasPermi="['shebao:management:suspension:add']"
        >添加</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyTypes" min-width="180" />
      <el-table-column label="暂停补贴类型" prop="pausedSubsidyTypes" min-width="180" />
      <el-table-column label="暂停年月" align="center" width="120">
        <template slot-scope="scope">
          {{ formatMonthText(scope.row.pauseMonth) }}
        </template>
      </el-table-column>
      <el-table-column label="暂停原因" min-width="140">
        <template slot-scope="scope">
          {{ pauseReasonLabel(scope.row.pauseReason) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="添加待遇暂停" :visible.sync="addOpen" width="1000px" append-to-body>
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

        <div class="section-title">待遇信息</div>
        <el-table :data="candidate.treatmentItems || []" border size="small">
          <el-table-column label="补贴类型" min-width="140">
            <template slot-scope="scope">
              {{ subsidyTypeLabel(scope.row.subsidyType) }}
            </template>
          </el-table-column>
          <el-table-column label="享受开始年月" width="140">
            <template slot-scope="scope">
              {{ formatMonthText(scope.row.benefitStartMonth) }}
            </template>
          </el-table-column>
          <el-table-column label="待遇发放状态" width="140">
            <template slot-scope="scope">
              <el-tag v-if="statusOf(scope.row) === '1'" type="danger">暂停</el-tag>
              <el-tag v-else type="success">正常</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-form ref="pauseFormRef" :model="pauseForm" :rules="pauseRules" label-width="110px" class="mt16">
          <div class="section-title">暂停信息录入</div>
          <el-form-item label="补贴类型" prop="determinationItemIds">
            <el-checkbox-group v-model="pauseForm.determinationItemIds">
              <el-checkbox
                v-for="item in candidate.treatmentItems || []"
                :key="item.determinationItemId"
                :label="item.determinationItemId"
              >
                {{ subsidyTypeLabel(item.subsidyType) }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="暂停年月" prop="pauseMonth">
            <el-date-picker
              v-model="pauseForm.pauseMonth"
              type="month"
              value-format="yyyy-MM"
              placeholder="选择暂停年月"
              style="width: 220px"
            />
          </el-form-item>
          <el-form-item label="暂停原因" prop="pauseReason">
            <el-select v-model="pauseForm.pauseReason" placeholder="请选择暂停原因" style="width: 220px">
              <el-option v-for="d in dict.type.pause_reason" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="pauseForm.remark" type="textarea" :rows="2" placeholder="请输入备注（选填）" />
          </el-form-item>
        </el-form>

        <div class="section-title">追回信息</div>
        <el-table :data="computedRecoverItems" border size="small">
          <el-table-column label="补贴类型" min-width="140">
            <template slot-scope="scope">
              {{ subsidyTypeLabel(scope.row.subsidyType) }}
            </template>
          </el-table-column>
          <el-table-column label="是否需要追回" width="120">
            <template slot-scope="scope">
              {{ scope.row.needRecover === '1' ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column label="追回开始年月" width="130">
            <template slot-scope="scope">
              {{ scope.row.recoverStartYm || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="追回终止年月" width="130">
            <template slot-scope="scope">
              {{ scope.row.recoverEndYm || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="需追回金额" min-width="140">
            <template slot-scope="scope">
              {{ scope.row.recoverAmount }} 元
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer">
        <el-button type="primary" :loading="submitLoading" @click="submitPause">保 存</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="待遇暂停详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <div v-if="detailData.id">
        <el-descriptions title="基本信息" :column="3" border size="small">
          <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
          <el-descriptions-item label="街道">{{ detailData.streetOfficeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="村委会">{{ detailData.villageCommitteeName || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">待遇信息</div>
        <el-table :data="detailData.treatmentItems || []" border size="small">
          <el-table-column label="补贴类型" min-width="140">
            <template slot-scope="scope">
              {{ subsidyTypeLabel(scope.row.subsidyType) }}
            </template>
          </el-table-column>
          <el-table-column label="享受开始年月" width="140">
            <template slot-scope="scope">
              {{ formatMonthText(scope.row.benefitStartMonth) }}
            </template>
          </el-table-column>
          <el-table-column label="待遇发放状态" width="140">
            <template slot-scope="scope">
              <el-tag v-if="statusOf(scope.row) === '1'" type="danger">暂停</el-tag>
              <el-tag v-else type="success">正常</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-descriptions title="暂停信息录入" :column="2" border size="small" class="mt16">
          <el-descriptions-item label="暂停补贴类型">{{ detailPausedTypes }}</el-descriptions-item>
          <el-descriptions-item label="暂停年月">{{ detailData.pauseMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="暂停原因">{{ pauseReasonLabel(detailData.pauseReason) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">追回信息</div>
        <el-table :data="detailData.recoverItems || []" border size="small">
          <el-table-column label="补贴类型" min-width="140">
            <template slot-scope="scope">
              {{ subsidyTypeLabel(scope.row.subsidyType) }}
            </template>
          </el-table-column>
          <el-table-column label="是否需要追回" width="120">
            <template slot-scope="scope">
              {{ scope.row.needRecover === '1' ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column label="追回开始年月" width="130">
            <template slot-scope="scope">
              {{ formatMonthText(scope.row.recoverStartMonth) }}
            </template>
          </el-table-column>
          <el-table-column label="追回终止年月" width="130">
            <template slot-scope="scope">
              {{ formatMonthText(scope.row.recoverEndMonth) }}
            </template>
          </el-table-column>
          <el-table-column label="需追回金额" min-width="140">
            <template slot-scope="scope">
              {{ money(scope.row.recoverAmount) }} 元
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { selectDictLabel } from '@/utils/ruoyi'
import {
  addBenefitSuspension,
  getBenefitSuspensionDetail,
  getSuspensionCandidate,
  listBenefitSuspension
} from '@/api/shebao/benefitSuspension'

export default {
  name: 'BenefitSuspension',
  dicts: ['pause_reason'],
  data() {
    return {
      loading: true,
      submitLoading: false,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, idCardNo: null, pauseReason: null },
      addOpen: false,
      detailOpen: false,
      candidateQueryIdCardNo: null,
      candidate: null,
      detailData: {},
      pauseForm: {
        determinationItemIds: [],
        pauseMonth: null,
        pauseReason: null,
        remark: null
      },
      pauseRules: {
        determinationItemIds: [{ required: true, message: '请选择补贴类型', trigger: 'change' }],
        pauseMonth: [{ required: true, message: '请选择暂停年月', trigger: 'change' }],
        pauseReason: [{ required: true, message: '请选择暂停原因', trigger: 'change' }]
      }
    }
  },
  computed: {
    computedRecoverItems() {
      const itemMap = new Map()
      ;(this.candidate && this.candidate.treatmentItems ? this.candidate.treatmentItems : []).forEach(item => {
        itemMap.set(item.determinationItemId, item)
      })
      const pauseYm = this.parseYearMonth(this.pauseForm.pauseMonth)
      return (this.pauseForm.determinationItemIds || []).map(itemId => {
        const item = itemMap.get(itemId) || {}
        const currentYm = this.currentYearMonth()
        const needRecover = pauseYm && this.compareYm(pauseYm, currentYm) < 0 ? '1' : '0'
        let recoverEnd = null
        let recoverMonths = 0
        if (needRecover === '1') {
          recoverEnd = this.minusOneMonth(currentYm)
          recoverMonths = this.monthDiffInclusive(pauseYm, recoverEnd)
        }
        const standard = Number(item.subsidyStandard || 0)
        const amount = (recoverMonths * standard).toFixed(2)
        return {
          subsidyType: item.subsidyType,
          needRecover,
          recoverStartYm: pauseYm ? this.ymText(pauseYm.year, pauseYm.month) : '-',
          recoverEndYm: recoverEnd ? this.ymText(recoverEnd.year, recoverEnd.month) : '-',
          recoverAmount: amount
        }
      })
    },
    detailPausedTypes() {
      return (this.detailData.recoverItems || [])
        .map(item => this.subsidyTypeLabel(item.subsidyType))
        .join('、') || '-'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitSuspension(this.queryParams).then(response => {
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
      this.pauseForm = {
        determinationItemIds: [],
        pauseMonth: null,
        pauseReason: null,
        remark: null
      }
    },
    searchCandidate() {
      if (!this.candidateQueryIdCardNo) {
        this.$modal.msgWarning('请输入身份证号')
        return
      }
      getSuspensionCandidate(this.candidateQueryIdCardNo).then(response => {
        this.candidate = response.data
        this.pauseForm.determinationItemIds = (this.candidate.treatmentItems || []).map(item => item.determinationItemId)
      }).catch(() => {
        this.candidate = null
      })
    },
    submitPause() {
      if (!this.candidate) {
        this.$modal.msgWarning('请先按身份证搜索待遇人员')
        return
      }
      this.$refs.pauseFormRef.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        addBenefitSuspension({
          determinationId: this.candidate.determinationId,
          subsidyPersonId: this.candidate.subsidyPersonId,
          idCardNo: this.candidate.idCardNo,
          determinationItemIds: this.pauseForm.determinationItemIds,
          pauseMonth: this.pauseForm.pauseMonth,
          pauseReason: this.pauseForm.pauseReason,
          remark: this.pauseForm.remark
        }).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.addOpen = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    },
    openDetail(row) {
      getBenefitSuspensionDetail(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
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
    pauseReasonLabel(value) {
      return selectDictLabel(this.dict.type.pause_reason || [], value) || value || '-'
    },
    formatMonthText(value) {
      if (!value) return '-'
      if (typeof value === 'string') return value.slice(0, 7)
      if (value instanceof Date) {
        return `${value.getFullYear()}-${String(value.getMonth() + 1).padStart(2, '0')}`
      }
      return '-'
    },
    statusOf(item) {
      return item && item.benefitStatus ? item.benefitStatus : '0'
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
      if (!start || !end) return 0
      const diff = (end.year - start.year) * 12 + (end.month - start.month)
      return diff >= 0 ? diff + 1 : 0
    },
    ymText(year, month) {
      if (!year || !month) return '-'
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

.mt16 {
  margin-top: 16px;
}

.mb8 {
  margin-bottom: 8px;
}

.mb10 {
  margin-bottom: 10px;
}
</style>
