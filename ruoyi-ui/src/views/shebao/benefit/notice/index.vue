<template>
  <div class="app-container" data-testid="benefit-notice-page">
    <el-card class="mb16">
      <div slot="header">生成预到龄通知批次</div>
      <el-form :model="generateForm" label-width="110px" inline>
        <el-form-item label="通知月份">
          <el-date-picker v-model="generateForm.noticeMonth" type="month" value-format="yyyy-MM" placeholder="请选择月份" data-testid="notice-generate-month" />
        </el-form-item>
        <el-form-item label="年龄阈值">
          <el-input-number v-model="generateForm.ageThreshold" :min="50" :max="80" data-testid="notice-generate-age-threshold" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="generateLoading" @click="handleGenerate" v-hasPermi="['shebao:benefit:notice:generate']" data-testid="notice-generate-submit">生成通知</el-button>
        </el-form-item>
        <el-form-item>
          <span class="form-tip">通知月份 + 3个月 = 实际到龄月份</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="通知月份">
        <el-date-picker v-model="queryParams.noticeMonth" type="month" value-format="yyyy-MM" data-testid="notice-query-month" />
      </el-form-item>
      <el-form-item label="批次号">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable data-testid="notice-query-batch-no" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.batchStatus" clearable placeholder="全部" data-testid="notice-query-status">
          <el-option label="已生成" value="generated" />
          <el-option label="处理中" value="processing" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" data-testid="notice-query-search">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" data-testid="notice-query-reset">重置</el-button>
        <el-button type="success" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['shebao:benefit:notice:list']" data-testid="notice-query-export">导出清单</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12" class="mb16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.totalCount }}</div>
          <div class="stat-label">总人数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value warning">{{ summary.pendingReviewCount }}</div>
          <div class="stat-label">待复核</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value success">{{ summary.approvedCount }}</div>
          <div class="stat-label">已通过</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value danger">{{ summary.rejectedCount }}</div>
          <div class="stat-label">已驳回</div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList" data-testid="notice-batch-table">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" min-width="180" />
      <el-table-column label="通知月份" prop="noticeMonth" width="110" />
      <el-table-column label="到龄月份" prop="retirementMonth" width="110" />
      <el-table-column label="总人数" prop="totalCount" width="80" />
      <el-table-column label="待复核" prop="pendingReviewCount" width="80" />
      <el-table-column label="已通过" prop="approvedCount" width="80" />
      <el-table-column label="已驳回" prop="rejectedCount" width="80" />
      <el-table-column label="批次状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="batchStatusType(scope.row.batchStatus)">{{ batchStatusText(scope.row.batchStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生成人" prop="createBy" width="120" />
      <el-table-column label="生成时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="openDetail(scope.row)" v-hasPermi="['shebao:benefit:notice:list']" :data-testid="`notice-view-${scope.row.batchNo}`">查看明细</el-button>
          <el-button size="mini" type="text" @click="handleExport(scope.row)" v-hasPermi="['shebao:benefit:notice:list']" :data-testid="`notice-export-${scope.row.batchNo}`">导出</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="通知批次明细" :visible.sync="detailOpen" width="1200px" data-testid="notice-detail-dialog">
      <el-descriptions :column="4" border v-if="currentBatch.batchNo">
        <el-descriptions-item label="批次号">{{ currentBatch.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="通知月份">{{ currentBatch.noticeMonth }}</el-descriptions-item>
        <el-descriptions-item label="到龄月份">{{ currentBatch.retirementMonth }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ batchStatusText(currentBatch.batchStatus) }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailList" v-loading="detailLoading" max-height="420" class="mt12" data-testid="notice-detail-table">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column label="用户ID" prop="subsidyPersonId" width="100" />
        <el-table-column label="用户编号" prop="userCode" width="120" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="身份证号" prop="idCardNo" min-width="180" />
        <el-table-column label="补贴类型" width="140">
          <template slot-scope="scope">
            <span>{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到龄日期" prop="retirementDate" width="110" />
        <el-table-column label="核定状态" width="110">
          <template slot-scope="scope">
            <el-tag :type="approvalStatusType(scope.row.determinationStatus)">{{ approvalStatusText(scope.row.determinationStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="材料状态" width="110">
          <template slot-scope="scope">
            <el-tag :type="scope.row.materialStatus === 'uploaded' ? 'success' : 'info'">{{ scope.row.materialStatus === 'uploaded' ? '已上传' : '待上传' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { generateBenefitNotice, listBenefitNoticeBatch, listBenefitNoticeDetail } from '@/api/shebao/benefit'

export default {
  name: 'BenefitNotice',
  data() {
    return {
      loading: false,
      detailLoading: false,
      generateLoading: false,
      total: 0,
      dataList: [],
      detailList: [],
      detailOpen: false,
      currentBatch: {},
      generateForm: { noticeMonth: null, ageThreshold: 60 },
      queryParams: { pageNum: 1, pageSize: 10, noticeMonth: null, batchNo: null, batchStatus: null }
    }
  },
  computed: {
    summary() {
      return this.dataList.reduce((acc, item) => {
        acc.totalCount += item.totalCount || 0
        acc.pendingReviewCount += item.pendingReviewCount || 0
        acc.approvedCount += item.approvedCount || 0
        acc.rejectedCount += item.rejectedCount || 0
        return acc
      }, { totalCount: 0, pendingReviewCount: 0, approvedCount: 0, rejectedCount: 0 })
    }
  },
  created() {
    const now = new Date()
    this.generateForm.noticeMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitNoticeBatch(this.queryParams).then(response => {
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
    handleGenerate() {
      if (!this.generateForm.noticeMonth) {
        this.$modal.msgWarning('请选择通知月份')
        return
      }
      this.$modal.confirm(`是否生成 ${this.generateForm.noticeMonth} 的预到龄通知批次？`).then(() => {
        this.generateLoading = true
        return generateBenefitNotice(this.generateForm)
      }).then(res => {
        this.$modal.msgSuccess(res.msg || '生成成功')
        this.queryParams.noticeMonth = this.generateForm.noticeMonth
        this.getList()
      }).finally(() => {
        this.generateLoading = false
      })
    },
    openDetail(row) {
      this.currentBatch = row
      this.detailOpen = true
      this.detailLoading = true
      listBenefitNoticeDetail({ batchNo: row.batchNo }).then(response => {
        this.detailList = response.rows || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    handleExport(row) {
      const params = row ? { batchNo: row.batchNo, noticeMonth: row.noticeMonth } : { ...this.queryParams }
      const name = row ? `预到龄通知_${row.batchNo}.xlsx` : `预到龄通知_${new Date().getTime()}.xlsx`
      this.download('shebao/benefit/notice/export', params, name)
    },
    batchStatusText(status) {
      return { generated: '已生成', processing: '处理中', completed: '已完成' }[status] || '未知'
    },
    batchStatusType(status) {
      return { generated: 'info', processing: 'warning', completed: 'success' }[status] || 'info'
    },
    getSubsidyTypeLabel(subsidyType) {
      return {
        land_loss_resident: '失地居民',
        demolition_resident: '拆迁居民',
        village_official: '村干部',
        expropriatee_subsidy: '被征地居民',
        teacher_subsidy: '教师'
      }[subsidyType] || subsidyType || '未知'
    },
    approvalStatusText(status) {
      return { draft: '待录入', pending_review: '待复核', approved: '已通过', rejected: '已驳回', payment_generated: '已进计划' }[status] || '未知'
    },
    approvalStatusType(status) {
      return { draft: 'info', pending_review: 'warning', approved: 'success', rejected: 'danger', payment_generated: 'success' }[status] || 'info'
    }
  }
}
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}
.mt12 {
  margin-top: 12px;
}
.form-tip {
  color: #909399;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}
.stat-value.warning {
  color: #e6a23c;
}
.stat-value.success {
  color: #67c23a;
}
.stat-value.danger {
  color: #f56c6c;
}
.stat-label {
  margin-top: 6px;
  color: #909399;
}
</style>
