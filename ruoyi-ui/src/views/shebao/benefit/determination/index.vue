<template>
  <div class="app-container" data-testid="benefit-determination-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="通知批次号">
        <el-input v-model="queryParams.noticeBatchNo" placeholder="请输入通知批次号" clearable data-testid="determination-query-batch-no" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable data-testid="determination-query-name" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable data-testid="determination-query-id-card" />
      </el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.approvalStatus" clearable placeholder="全部" data-testid="determination-query-approval-status">
          <el-option label="待录入" value="draft" />
          <el-option label="待复核" value="pending_review" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="材料状态">
        <el-select v-model="queryParams.materialStatus" clearable placeholder="全部" data-testid="determination-query-material-status">
          <el-option label="待上传" value="pending_upload" />
          <el-option label="已上传" value="uploaded" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" data-testid="determination-query-search">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" data-testid="determination-query-reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-upload" size="mini" @click="handleBatchImport" v-hasPermi="['shebao:benefit:determination:import']" data-testid="determination-batch-import-open">批量导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-download" size="mini" @click="downloadTemplate" v-hasPermi="['shebao:benefit:determination:import']" data-testid="determination-download-template">下载模板</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" data-testid="determination-table">
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
      <el-table-column label="银行名称" prop="bankName" width="140" />
      <el-table-column label="银行卡号" prop="bankAccount" min-width="180" />
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
      <el-table-column label="操作" align="center" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)" :data-testid="`determination-view-${scope.row.id}`">详情</el-button>
          <el-button size="mini" type="text" @click="handleEdit(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'" :data-testid="`determination-edit-${scope.row.id}`">录入/修改</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'" :data-testid="`determination-submit-${scope.row.id}`">提交复核</el-button>
          <el-button size="mini" type="text" @click="handlePrint(scope.row)" v-hasPermi="['shebao:benefit:determination:print']" :data-testid="`determination-print-${scope.row.id}`">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="900px" data-testid="determination-edit-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="通知批次号">
              <el-input v-model="form.noticeBatchNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="form.name" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入身份证号" data-testid="determination-form-id-card" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="补贴类型" prop="subsidyType">
              <el-input :value="getSubsidyTypeLabel(form.subsidyType)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="补贴标准" prop="subsidyStandard">
              <el-input-number v-model="form.subsidyStandard" :min="0" :precision="2" style="width: 100%" data-testid="determination-form-standard" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="享受开始年月" prop="startMonth">
              <el-date-picker v-model="form.startMonth" type="month" value-format="yyyy-MM" style="width: 100%" data-testid="determination-form-start-month" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="银行名称" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入银行名称" data-testid="determination-form-bank-name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行卡号" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行卡号" data-testid="determination-form-bank-account" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="账户名" prop="bankAccountName">
              <el-input v-model="form.bankAccountName" placeholder="请输入账户名" data-testid="determination-form-bank-account-name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="材料状态">
              <el-tag :type="form.materialStatus === 'uploaded' ? 'success' : 'info'">{{ form.materialStatus === 'uploaded' ? '已上传' : '待上传' }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="证明材料ZIP">
          <el-upload :auto-upload="false" :limit="1" :file-list="attachmentFileList" accept=".zip" :on-change="handleAttachmentChange" :on-remove="handleAttachmentRemove" data-testid="determination-attachment-upload">
            <el-button size="mini" type="primary" data-testid="determination-attachment-select">选择ZIP</el-button>
            <div slot="tip" class="el-upload__tip">文件名必须为“用户ID.zip”</div>
          </el-upload>
          <el-button size="mini" type="success" class="mt8" :disabled="!selectedAttachment" @click="handleUploadAttachment" data-testid="determination-attachment-submit">上传并解压</el-button>
        </el-form-item>
        <el-form-item label="图片预览" v-if="form.materialImagePaths && form.materialImagePaths.length">
          <div class="preview-list">
            <image-preview v-for="(item, index) in form.materialImagePaths" :key="index" :src="item" :width="100" :height="100" class="preview-item" />
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="submitLoading" @click="submitForm" data-testid="determination-form-save">保存</el-button>
        <el-button @click="cancel" data-testid="determination-form-cancel">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="核定详情" :visible.sync="detailOpen" width="900px" data-testid="determination-detail-dialog">
      <el-descriptions :column="2" border v-if="detailData.id">
        <el-descriptions-item label="通知批次号">{{ detailData.noticeBatchNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="补贴类型">{{ getSubsidyTypeLabel(detailData.subsidyType) }}</el-descriptions-item>
        <el-descriptions-item label="银行名称">{{ detailData.bankName }}</el-descriptions-item>
        <el-descriptions-item label="银行卡号">{{ detailData.bankAccount }}</el-descriptions-item>
        <el-descriptions-item label="账户名">{{ detailData.bankAccountName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.approvalStatus }}</el-descriptions-item>
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

    <el-dialog title="批量导入待遇核定" :visible.sync="importOpen" width="560px" data-testid="determination-import-dialog">
      <el-form label-width="110px">
        <el-form-item label="通知批次号">
          <el-input v-model="importForm.noticeBatchNo" placeholder="可为空，优先读取Excel中的批次号" data-testid="determination-import-batch-no" />
        </el-form-item>
        <el-form-item label="Excel文件">
          <el-upload :auto-upload="false" :limit="1" :file-list="importFileList" accept=".xls,.xlsx" :on-change="handleImportFileChange" :on-remove="handleImportFileRemove" data-testid="determination-import-excel-upload">
            <el-button size="mini" type="primary" data-testid="determination-import-excel-select">选择Excel</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="材料ZIP">
          <el-upload :auto-upload="false" multiple :file-list="importAttachmentList" accept=".zip" :on-change="handleImportAttachmentChange" :on-remove="handleImportAttachmentRemove" data-testid="determination-import-zip-upload">
            <el-button size="mini" type="success" data-testid="determination-import-zip-select">选择多个ZIP</el-button>
            <div slot="tip" class="el-upload__tip">每个ZIP名称需为“用户ID.zip”</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="downloadTemplate" data-testid="determination-import-download-template">下载模板</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport" data-testid="determination-import-submit">开始导入</el-button>
        <el-button @click="importOpen = false" data-testid="determination-import-cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { batchBenefitDetermination, getBenefitDetermination, listBenefitDetermination, submitBenefitDetermination, updateBenefitDetermination, uploadBenefitAttachment } from '@/api/shebao/benefit'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ImagePreview from '@/components/ImagePreview'

export default {
  name: 'BenefitDetermination',
  dicts: ['subsidy_type'],
  components: { ApprovalStatus, ImagePreview },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      importLoading: false,
      submitLoading: false,
      showSearch: true,
      total: 0,
      dataList: [],
      title: '待遇核定录入',
      open: false,
      detailOpen: false,
      importOpen: false,
      form: {},
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeBatchNo: null,
        name: null,
        idCardNo: null,
        approvalStatus: null,
        materialStatus: null
      },
      selectedAttachment: null,
      attachmentFileList: [],
      importForm: {
        noticeBatchNo: null,
        file: null,
        attachments: []
      },
      importFileList: [],
      importAttachmentList: [],
      rules: {
        idCardNo: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }],
        subsidyStandard: [{ required: true, message: '补贴标准不能为空', trigger: 'blur' }],
        startMonth: [{ required: true, message: '享受开始年月不能为空', trigger: 'change' }],
        bankName: [{ required: true, message: '银行名称不能为空', trigger: 'blur' }],
        bankAccount: [{ required: true, message: '银行卡号不能为空', trigger: 'blur' }],
        bankAccountName: [{ required: true, message: '账户名不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    if (this.$route.query.noticeBatchNo) {
      this.queryParams.noticeBatchNo = this.$route.query.noticeBatchNo
      this.importForm.noticeBatchNo = this.$route.query.noticeBatchNo
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitDetermination(this.queryParams).then(response => {
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
    handleBatchImport() {
      this.importOpen = true
      this.importForm.noticeBatchNo = this.queryParams.noticeBatchNo
    },
    downloadTemplate() {
      this.download('shebao/benefit/determination/importTemplate', {}, `待遇核定导入模板_${new Date().getTime()}.xlsx`)
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
    handleEdit(row) {
      this.title = '待遇核定录入'
      this.loadDetail(row.id, true)
    },
    handleView(row) {
      this.loadDetail(row.id, false)
    },
    loadDetail(id, editable) {
      getBenefitDetermination(id).then(response => {
        const data = response.data || {}
        if (editable) {
          this.form = {
            ...data,
            startMonth: data.benefitStartYear && data.benefitStartMonth ? `${data.benefitStartYear}-${String(data.benefitStartMonth).padStart(2, '0')}` : null
          }
          this.selectedAttachment = null
          this.attachmentFileList = []
          this.open = true
        } else {
          this.detailData = data
          this.detailOpen = true
        }
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = {
          id: this.form.id,
          subsidyPersonId: this.form.subsidyPersonId,
          noticeBatchNo: this.form.noticeBatchNo,
          noticeDetailId: this.form.noticeDetailId,
          subsidyType: this.form.subsidyType,
          idCardNo: this.form.idCardNo,
          bankName: this.form.bankName,
          bankAccount: this.form.bankAccount,
          bankAccountName: this.form.bankAccountName,
          subsidyStandard: this.form.subsidyStandard
        }
        if (this.form.startMonth) {
          const parts = this.form.startMonth.split('-')
          data.benefitStartYear = parseInt(parts[0])
          data.benefitStartMonth = parseInt(parts[1])
        }
        updateBenefitDetermination(data).then(() => {
          if (this.selectedAttachment) {
            return uploadBenefitAttachment(this.form.id, this.selectedAttachment)
          }
          return Promise.resolve()
        }).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    },
    handleUploadAttachment() {
      if (!this.selectedAttachment || !this.form.id) {
        this.$modal.msgWarning('请先选择ZIP文件')
        return
      }
      uploadBenefitAttachment(this.form.id, this.selectedAttachment).then(() => {
        this.$modal.msgSuccess('材料上传成功')
        return getBenefitDetermination(this.form.id)
      }).then(response => {
        const data = response.data || {}
        this.form = {
          ...this.form,
          materialStatus: data.materialStatus,
          materialImagePaths: data.materialImagePaths || [],
          materialZipPath: data.materialZipPath
        }
      })
    },
    handleSubmit(row) {
      this.$modal.confirm('确认提交该待遇核定进入复核吗？').then(() => {
        return submitBenefitDetermination(row.id)
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handlePrint(row) {
      this.download(`shebao/benefit/determination/print/${row.id}`, {}, `待遇核定表_${row.id}_${new Date().getTime()}.xlsx`)
    },
    cancel() {
      this.open = false
      this.form = {}
      this.selectedAttachment = null
      this.attachmentFileList = []
    },
    handleAttachmentChange(file, fileList) {
      this.selectedAttachment = file.raw
      this.attachmentFileList = fileList.slice(-1)
    },
    handleAttachmentRemove() {
      this.selectedAttachment = null
      this.attachmentFileList = []
    },
    handleImportFileChange(file, fileList) {
      this.importForm.file = file.raw
      this.importFileList = fileList.slice(-1)
    },
    handleImportFileRemove() {
      this.importForm.file = null
      this.importFileList = []
    },
    handleImportAttachmentChange(file, fileList) {
      this.importForm.attachments = fileList.map(item => item.raw).filter(Boolean)
      this.importAttachmentList = fileList
    },
    handleImportAttachmentRemove(file, fileList) {
      this.importForm.attachments = fileList.map(item => item.raw).filter(Boolean)
      this.importAttachmentList = fileList
    },
    submitImport() {
      if (!this.importForm.file) {
        this.$modal.msgWarning('请先选择Excel文件')
        return
      }
      this.importLoading = true
      batchBenefitDetermination(this.importForm).then(res => {
        this.$modal.msgSuccess(res.msg || '导入成功')
        this.importOpen = false
        this.importForm = { noticeBatchNo: this.queryParams.noticeBatchNo, file: null, attachments: [] }
        this.importFileList = []
        this.importAttachmentList = []
        this.getList()
      }).finally(() => {
        this.importLoading = false
      })
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.mt8 {
  margin-top: 8px;
}
.mt12 {
  margin-top: 12px;
}
.preview-list {
  display: flex;
  flex-wrap: wrap;
}
.preview-item {
  margin-right: 12px;
  margin-bottom: 12px;
}
.section-title {
  margin-bottom: 8px;
  font-weight: 600;
}
</style>
