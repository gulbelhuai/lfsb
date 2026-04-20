<template>
  <div class="app-container" data-testid="benefit-review-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" clearable data-testid="review-query-name" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" clearable />
      </el-form-item>
      <el-form-item label="补贴类型" v-show="false">
        <el-input v-model="queryParams.subsidyType" clearable data-testid="review-query-subsidy-type" placeholder="列表聚合类型或明细编码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" data-testid="review-query-search">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" data-testid="review-query-reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-check" size="mini" :disabled="multiple" @click="handleBatchApprove" v-hasPermi="['shebao:benefit:review:approve']" data-testid="review-batch-approve">批量通过</el-button>
      </el-col>
    </el-row>

    <el-table class="rx-table--compact" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange" data-testid="review-table">
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" min-width="180" />
      <el-table-column label="补贴类型" min-width="200">
        <template slot-scope="scope">
          <span>{{ formatSubsidyTypeMix(scope.row.subsidyType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="银行账号" prop="bankAccount" min-width="180" />
      <el-table-column label="材料状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.materialStatus === 'uploaded' ? 'success' : 'info'">{{ scope.row.materialStatus === 'uploaded' ? '已上传' : '待上传' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)" :data-testid="`review-view-${scope.row.id}`">抽查详情</el-button>
          <el-button size="mini" type="success" @click="handleReview(scope.row, true)" :data-testid="`review-pass-${scope.row.id}`">通过</el-button>
          <el-button size="mini" type="danger" @click="handleReview(scope.row, false)" :data-testid="`review-reject-${scope.row.id}`">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="核定抽查详情" :visible.sync="detailOpen" width="1100px" data-testid="review-detail-dialog">
      <el-card shadow="never" class="mb8" v-if="detailData.id">
        <div slot="header" class="section-title">人员基本信息</div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="姓名">{{ detailData.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detailData.gender === '1' ? '男' : detailData.gender === '2' ? '女' : (detailData.gender || '-') }}</el-descriptions-item>
          <el-descriptions-item label="出生年月">{{ detailData.birthday || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户编号">{{ detailData.userCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailData.idCardNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属街道办">{{ detailData.streetOfficeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属村委会">{{ detailData.villageCommitteeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailData.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="户籍所在地" :span="3">{{ detailData.householdRegistration || '-' }}</el-descriptions-item>
          <el-descriptions-item label="家庭住址" :span="3">{{ detailData.homeAddress || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never" class="mb8" v-if="detailData.id">
        <div slot="header" class="section-title">社保信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="参保状态">{{ detailData.subsidyStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员状态">{{ detailData.personStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="参加城乡居保">{{ ynLabel(detailData.joinUrbanRuralInsurance) }}</el-descriptions-item>
          <el-descriptions-item label="参加职工养老">{{ ynLabel(detailData.joinEmployeePension) }}</el-descriptions-item>
          <el-descriptions-item label="已领职工养老待遇">{{ ynLabel(detailData.hasEmployeePension) }}</el-descriptions-item>
          <el-descriptions-item label="职工养老月数">{{ detailData.employeePensionMonths !== null && detailData.employeePensionMonths !== undefined ? detailData.employeePensionMonths : '-' }}</el-descriptions-item>
          <el-descriptions-item label="灵活就业养老月数">{{ detailData.flexibleEmploymentMonths !== null && detailData.flexibleEmploymentMonths !== undefined ? detailData.flexibleEmploymentMonths : '-' }}</el-descriptions-item>
          <el-descriptions-item label="困难补贴月数">{{ detailData.difficultySubsidyMonths !== null && detailData.difficultySubsidyMonths !== undefined ? detailData.difficultySubsidyMonths : '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never" class="mb8" v-if="detailData.id">
        <div slot="header" class="section-title">发放方式</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="发放机构">{{ grantOrgLabel(detailData.grantOrg) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开户名">{{ detailData.accountName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="与参保人关系">{{ detailData.relationToInsured || '-' }}</el-descriptions-item>
          <el-descriptions-item label="银行账号">{{ detailData.bankAccount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailData.grantRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never" class="mb8" v-if="(detailData.items || []).length">
        <div slot="header" class="section-title">补贴与待遇信息</div>
        <el-table class="rx-table--compact" :data="detailData.items || []" border size="mini">
          <el-table-column label="补贴类型" width="120">
            <template slot-scope="scope">{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</template>
          </el-table-column>
          <el-table-column label="认定时所在村街" prop="villageStreet" min-width="120" />
          <el-table-column label="征地/拆迁时间" prop="eventDate" width="120" />
          <el-table-column label="补贴标准" prop="subsidyStandard" width="100" />
          <el-table-column label="享受开始年月" min-width="120">
            <template slot-scope="scope">{{ formatStartMonth(scope.row) || '-' }}</template>
          </el-table-column>
          <el-table-column label="补发月数" prop="benefitMonths" width="90" />
          <el-table-column label="补发金额" prop="benefitAmount" width="100" />
        </el-table>
      </el-card>

      <el-card shadow="never" class="mb8" v-if="detailData.id">
        <div slot="header" class="section-title">审批信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="审批状态">{{ approvalStatusLabel(detailData.approvalStatus) }}</el-descriptions-item>
          <el-descriptions-item label="材料状态">{{ materialStatusLabel(detailData.materialStatus) }}</el-descriptions-item>
          <el-descriptions-item label="到龄年月">{{ formatEligibleMonth(detailData) }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detailData.submitTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ detailData.submitBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复核时间">{{ detailData.reviewTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复核人">{{ detailData.reviewBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复核意见" :span="2">{{ detailData.reviewRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <div class="mt12" v-if="detailData.materialImagePaths && detailData.materialImagePaths.length">
        <div class="section-title">证明材料预览</div>
        <div class="preview-list">
          <image-preview v-for="(item, index) in detailData.materialImagePaths" :key="index" :src="item" :width="120" :height="120" class="preview-item" />
        </div>
      </div>
      <div class="mt12" v-if="detailData.materialZipPath">
        <el-link :href="baseUrl + detailData.materialZipPath" target="_blank" type="primary">下载原始ZIP</el-link>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { batchApproveBenefitReview, getBenefitDetermination, listBenefitReview, reviewBenefitPass, reviewBenefitReject } from '@/api/shebao/benefit'
import { selectDictLabel } from '@/utils/ruoyi'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ImagePreview from '@/components/ImagePreview'

export default {
  name: 'BenefitReview',
  dicts: ['shebao_grant_org'],
  components: { ApprovalStatus, ImagePreview },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      total: 0,
      dataList: [],
      ids: [],
      multiple: true,
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        subsidyType: null,
        approvalStatus: 'pending_review'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    grantOrgLabel(val) {
      return selectDictLabel(this.dict.type.shebao_grant_org || [], val)
    },
    getSubsidyTypeLabel(subsidyType) {
      return {
        land_loss: '失地补贴',
        demolition: '拆迁补贴',
        village_official: '村干部补贴',
        land_loss_resident: '失地补贴',
        demolition_resident: '拆迁补贴',
        expropriatee_subsidy: '被征地居民',
        teacher_subsidy: '教师'
      }[subsidyType] || subsidyType || '未知'
    },
    formatSubsidyTypeMix(text) {
      if (!text) return '-'
      return text.split('/').map(s => this.getSubsidyTypeLabel(s)).join(' / ')
    },
    formatStartMonth(row) {
      if (!row) return ''
      if (row.benefitStartYear != null && row.benefitStartMonth != null) {
        return `${row.benefitStartYear}-${String(row.benefitStartMonth).padStart(2, '0')}`
      }
      return ''
    },
    ynLabel(v) {
      if (v === '1' || v === 1) return '是'
      if (v === '0' || v === 0) return '否'
      return v || '-'
    },
    approvalStatusLabel(status) {
      return {
        draft: '待录入',
        pending_review: '待复核',
        approved: '已通过',
        rejected: '已驳回'
      }[status] || status || '-'
    },
    materialStatusLabel(status) {
      return {
        pending_upload: '待上传',
        uploaded: '已上传',
        verified: '已核验'
      }[status] || status || '-'
    },
    formatEligibleMonth(row) {
      if (!row || !row.eligibleYear || !row.eligibleMonth) return '-'
      return `${row.eligibleYear}-${String(row.eligibleMonth).padStart(2, '0')}`
    },
    getList() {
      this.loading = true
      listBenefitReview(this.queryParams).then(response => {
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
      this.queryParams.approvalStatus = 'pending_review'
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    handleView(row) {
      getBenefitDetermination(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    handleReview(row, approved) {
      const func = approved ? reviewBenefitPass : reviewBenefitReject
      const msg = approved ? '通过' : '驳回'
      this.$prompt(`请输入${msg}意见`, '审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: approved ? null : /.+/,
        inputErrorMessage: approved ? '' : '请输入驳回原因'
      }).then(({ value }) => {
        return func(row.id, value || '')
      }).then(() => {
        this.$modal.msgSuccess(`${msg}成功`)
        this.getList()
      })
    },
    handleBatchApprove() {
      this.$prompt('请输入批量通过意见（选填）', '批量审核通过', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        return batchApproveBenefitReview({
          ids: this.ids,
          remark: value || ''
        })
      }).then(() => {
        this.$modal.msgSuccess('批量审核通过成功')
        this.getList()
      })
    },
    goPaymentPlan() {
      this.$router.push({ path: '/shebao/payment/plan' })
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.mt12 {
  margin-top: 12px;
}
.section-title {
  margin-bottom: 8px;
  font-weight: 600;
}
.preview-list {
  display: flex;
  flex-wrap: wrap;
}
.preview-item {
  margin-right: 12px;
  margin-bottom: 12px;
}
</style>
