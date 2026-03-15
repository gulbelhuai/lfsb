<template>
  <div class="app-container" data-testid="benefit-review-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="通知批次号">
        <el-input v-model="queryParams.noticeBatchNo" placeholder="请输入通知批次号" clearable data-testid="review-query-batch-no" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" clearable data-testid="review-query-name" />
      </el-form-item>
      <el-form-item label="补贴类型">
        <el-input v-model="queryParams.subsidyType" clearable data-testid="review-query-subsidy-type" />
      </el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.approvalStatus" placeholder="全部" clearable data-testid="review-query-approval-status">
          <el-option label="全部" value="" />
          <el-option label="草稿" value="draft" />
          <el-option label="待审核" value="pending_review" />
          <el-option label="已审核通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
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
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-connection" size="mini" :disabled="!queryParams.noticeBatchNo" @click="goPaymentPlan" data-testid="review-go-payment-plan">去支付计划</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange" data-testid="review-table">
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="通知批次号" prop="noticeBatchNo" min-width="180" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" min-width="180" />
      <el-table-column label="补贴类型" width="140">
        <template slot-scope="scope">
          <span>{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="补贴标准" prop="subsidyStandard" width="100" />
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
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)" :data-testid="`review-view-${scope.row.id}`">抽查详情</el-button>
          <el-button size="mini" type="success" @click="handleReview(scope.row, true)" :data-testid="`review-pass-${scope.row.id}`">通过</el-button>
          <el-button size="mini" type="danger" @click="handleReview(scope.row, false)" :data-testid="`review-reject-${scope.row.id}`">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="核定抽查详情" :visible.sync="detailOpen" width="900px" data-testid="review-detail-dialog">
      <el-descriptions :column="2" border v-if="detailData.id">
        <el-descriptions-item label="通知批次号">{{ detailData.noticeBatchNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="补贴类型">{{ getSubsidyTypeLabel(detailData.subsidyType) }}</el-descriptions-item>
        <el-descriptions-item label="银行名称">{{ detailData.bankName }}</el-descriptions-item>
        <el-descriptions-item label="银行卡号">{{ detailData.bankAccount }}</el-descriptions-item>
        <el-descriptions-item label="账户名">{{ detailData.bankAccountName }}</el-descriptions-item>
        <el-descriptions-item label="材料状态">{{ detailData.materialStatus }}</el-descriptions-item>
      </el-descriptions>
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
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ImagePreview from '@/components/ImagePreview'

export default {
  name: 'BenefitReview',
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
        noticeBatchNo: null, 
        name: null, 
        subsidyType: null, 
        approvalStatus: null  // 修改：初始值改为null，而不是pending_review
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getSubsidyTypeLabel(subsidyType) {
      return {
        land_loss_resident: '失地居民',
        demolition_resident: '拆迁居民',
        village_official: '村干部',
        expropriatee_subsidy: '被征地居民',
        teacher_subsidy: '教师'
      }[subsidyType] || subsidyType || '未知'
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
      // 重置后保持 null（查询全部），不设置为 'pending_review'
      this.queryParams.approvalStatus = null
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
          noticeBatchNo: this.queryParams.noticeBatchNo,
          ids: this.ids,
          remark: value || ''
        })
      }).then(() => {
        this.$modal.msgSuccess('批量审核通过成功')
        this.getList()
      })
    },
    goPaymentPlan() {
      if (!this.queryParams.noticeBatchNo) {
        this.$modal.msgWarning('请先输入通知批次号')
        return
      }
      this.$router.push({ path: '/shebao/payment/plan', query: { noticeBatchNo: this.queryParams.noticeBatchNo } })
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
