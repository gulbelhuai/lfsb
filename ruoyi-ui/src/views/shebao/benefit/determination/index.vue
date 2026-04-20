<template>
  <div class="app-container" data-testid="benefit-determination-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.approvalStatus" clearable placeholder="全部">
          <el-option label="待录入" value="draft" />
          <el-option label="待复核" value="pending_review" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="材料状态">
        <el-select v-model="queryParams.materialStatus" clearable placeholder="全部">
          <el-option label="待上传" value="pending_upload" />
          <el-option label="已上传" value="uploaded" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:benefit:determination:add']">添加</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table class="rx-table--compact" v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" min-width="180" />
      <el-table-column label="补贴类型" min-width="180">
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
      <el-table-column label="操作" align="center" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" @click="handleEdit(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">录入/修改</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">提交复核</el-button>
          <el-button size="mini" type="text" @click="handlePrint(scope.row)" v-hasPermi="['shebao:benefit:determination:print']">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="1000px">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-card shadow="never" class="mb8" v-if="!form.id">
          <div slot="header" class="section-title">查询待核定人员</div>
          <el-row :gutter="10">
            <el-col :span="10">
              <el-input v-model="searchIdCardNo" placeholder="请输入身份证号" />
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="handlePrepareByIdCard">查询</el-button>
            </el-col>
          </el-row>
        </el-card>

        <el-card shadow="never" class="mb8" v-if="personInfo.name">
          <div slot="header" class="section-title">人员基本信息</div>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{ personInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ personInfo.gender === '1' ? '男' : personInfo.gender === '2' ? '女' : personInfo.gender }}</el-descriptions-item>
            <el-descriptions-item label="出生年月">{{ personInfo.birthMonth }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ personInfo.idCardNo }}</el-descriptions-item>
            <el-descriptions-item label="所属街道办">{{ personInfo.streetOfficeName }}</el-descriptions-item>
            <el-descriptions-item label="所属村委会">{{ personInfo.villageCommitteeName }}</el-descriptions-item>
            <el-descriptions-item label="户籍所在地">{{ personInfo.householdRegistration }}</el-descriptions-item>
            <el-descriptions-item label="家庭住址">{{ personInfo.homeAddress }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ personInfo.phone }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="mb8" v-if="personInfo.name">
          <div slot="header" class="section-title">社保信息</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="参保状态">{{ socialInsurance.subsidyStatus }}</el-descriptions-item>
            <el-descriptions-item label="人员状态">{{ socialInsurance.personStatus }}</el-descriptions-item>
            <el-descriptions-item label="参加城乡居保">{{ ynLabel(socialInsurance.joinUrbanRuralInsurance) }}</el-descriptions-item>
            <el-descriptions-item label="参加职工养老">{{ ynLabel(socialInsurance.joinEmployeePension) }}</el-descriptions-item>
            <el-descriptions-item label="已领职工养老待遇">{{ ynLabel(socialInsurance.hasEmployeePension) }}</el-descriptions-item>
            <el-descriptions-item label="职工养老月数">{{ socialInsurance.employeePensionMonths }}</el-descriptions-item>
            <el-descriptions-item label="灵活就业养老月数">{{ socialInsurance.flexibleEmploymentMonths }}</el-descriptions-item>
            <el-descriptions-item label="困难补贴月数">{{ socialInsurance.difficultySubsidyMonths }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="mb8" v-if="subsidyRows.length">
          <div slot="header" class="section-title">补贴信息</div>
          <el-table class="rx-table--compact" :data="subsidyRows" border size="mini">
            <el-table-column label="补贴类型" width="120">
              <template slot-scope="scope">{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="认定时所在村街" prop="villageStreet" min-width="140" />
            <el-table-column label="征地/拆迁时间" min-width="120">
              <template slot-scope="scope">{{ scope.row.eventDate || '-' }}</template>
            </el-table-column>
            <el-table-column label="享受开始年月" min-width="180">
              <template slot-scope="scope">
                <el-date-picker v-model="scope.row.startMonth" type="month" value-format="yyyy-MM" style="width: 160px" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" class="mb8" v-if="personInfo.name">
          <div slot="header" class="section-title">发放方式</div>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="发放机构" prop="grantOrg">
                <el-select v-model="form.grantOrg" style="width: 100%" placeholder="请选择">
                  <el-option v-for="d in dict.type.shebao_grant_org" :key="d.value" :label="d.label" :value="d.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开户名" prop="accountName">
                <el-input v-model="form.accountName" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="与参保人关系" prop="relationToInsured">
                <el-input v-model="form.relationToInsured" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="银行账号" prop="bankAccount">
                <el-input v-model="form.bankAccount" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注">
            <el-input type="textarea" :rows="2" v-model="form.grantRemark" />
          </el-form-item>
        </el-card>

        <el-card shadow="never" class="mb8" v-if="subsidyRows.length">
          <div slot="header" class="section-title">待遇信息</div>
          <el-table class="rx-table--compact" :data="benefitRows" border size="mini">
            <el-table-column label="补贴类型" width="120">
              <template slot-scope="scope">{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="补贴标准" prop="subsidyStandard" width="120" />
            <el-table-column label="补发月数" prop="benefitMonths" width="100" />
            <el-table-column label="补发金额" prop="benefitAmount" width="120" />
          </el-table>
        </el-card>

        <el-form-item label="证明材料ZIP" v-if="form.id">
          <el-upload :auto-upload="false" :limit="1" :file-list="attachmentFileList" accept=".zip" :on-change="handleAttachmentChange" :on-remove="handleAttachmentRemove">
            <el-button size="mini" type="primary">选择ZIP</el-button>
            <!-- <div slot="tip" class="el-upload__tip">文件格式必须为zip</div> -->
          </el-upload>
          <el-button size="mini" type="success" class="mt8" :disabled="!selectedAttachment" @click="handleUploadAttachment">上传并解压</el-button>
        </el-form-item>
        <el-form-item label="图片预览" v-if="form.materialImagePaths && form.materialImagePaths.length">
          <div class="preview-list">
            <image-preview v-for="(item, index) in form.materialImagePaths" :key="index" :src="item" :width="100" :height="100" class="preview-item" />
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="核定详情" :visible.sync="detailOpen" width="1100px">
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
          <el-table-column label="补贴标准" prop="subsidyStandard" width="120" />
          <el-table-column label="享受开始年月" min-width="120">
            <template slot-scope="scope">{{ formatStartMonth(scope.row) || '-' }}</template>
          </el-table-column>
          <el-table-column label="补发月数" prop="benefitMonths" width="100" />
          <el-table-column label="补发金额" prop="benefitAmount" width="120" />
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
import { getBenefitDetermination, listBenefitDetermination, prepareBenefitDetermination, saveBenefitDeterminationDraft, submitBenefitDetermination, uploadBenefitAttachment } from '@/api/shebao/benefit'
import { selectDictLabel } from '@/utils/ruoyi'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ImagePreview from '@/components/ImagePreview'

export default {
  name: 'BenefitDetermination',
  dicts: ['subsidy_type', 'shebao_grant_org'],
  components: { ApprovalStatus, ImagePreview },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      submitLoading: false,
      showSearch: true,
      total: 0,
      dataList: [],
      title: '待遇核定录入',
      open: false,
      detailOpen: false,
      form: {},
      detailData: {},
      personInfo: {},
      socialInsurance: {},
      subsidyRows: [],
      searchIdCardNo: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        approvalStatus: null,
        materialStatus: null
      },
      selectedAttachment: null,
      attachmentFileList: [],
      rules: {
        grantOrg: [{ required: true, message: '发放机构不能为空', trigger: 'change' }],
        accountName: [{ required: true, message: '开户名不能为空', trigger: 'blur' }],
        relationToInsured: [{ required: true, message: '与参保人关系不能为空', trigger: 'blur' }],
        bankAccount: [{ required: true, message: '银行账号不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    benefitRows() {
      return (this.subsidyRows || []).map(item => {
        const months = this.monthDiff(item.startMonth, this.currentYm())
        const amount = this.mulMoney(item.subsidyStandard, months)
        return { ...item, benefitMonths: months, benefitAmount: amount }
      })
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitDetermination(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.title = '待遇核定新增'
      this.open = true
      this.form = {
        grantOrg: null,
        accountName: '',
        relationToInsured: '本人',
        bankAccount: null,
        grantRemark: null
      }
      this.personInfo = {}
      this.socialInsurance = {}
      this.subsidyRows = []
      this.searchIdCardNo = ''
      this.selectedAttachment = null
      this.attachmentFileList = []
    },
    handleEdit(row) {
      this.title = '待遇核定录入/修改'
      getBenefitDetermination(row.id).then(res => {
        const data = res.data || {}
        this.form = {
          id: data.id,
          subsidyPersonId: data.subsidyPersonId,
          idCardNo: data.idCardNo,
          grantOrg: data.grantOrg,
          accountName: data.accountName,
          relationToInsured: data.relationToInsured || '本人',
          bankAccount: data.bankAccount,
          grantRemark: data.grantRemark,
          materialStatus: data.materialStatus,
          materialImagePaths: data.materialImagePaths || [],
          materialZipPath: data.materialZipPath
        }
        this.subsidyRows = (data.items || []).map(it => ({
          subsidyType: it.subsidyType,
          villageStreet: it.villageStreet,
          eventDate: it.eventDate,
          subsidyStandard: it.subsidyStandard || 0,
          startMonth: this.formatStartMonth(it)
        }))
        return prepareBenefitDetermination(data.idCardNo)
      }).then(r2 => {
        const prep = r2.data || {}
        this.personInfo = prep.person || {}
        this.socialInsurance = prep.socialInsurance || {}
        this.searchIdCardNo = this.personInfo.idCardNo || ''
        this.open = true
      })
    },
    handleView(row) {
      getBenefitDetermination(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    handlePrepareByIdCard() {
      if (!this.searchIdCardNo) {
        this.$modal.msgWarning('请输入身份证号')
        return
      }
      prepareBenefitDetermination(this.searchIdCardNo).then(res => {
        const data = res.data || {}
        if (data.alreadyDetermined) {
          this.$modal.msgWarning(data.alreadyDeterminedMsg || '人员待遇已核定')
          return
        }
        this.personInfo = data.person || {}
        this.socialInsurance = data.socialInsurance || {}
        this.form.subsidyPersonId = this.personInfo.subsidyPersonId
        this.form.idCardNo = this.personInfo.idCardNo
        this.form.accountName = this.personInfo.name || ''
        this.subsidyRows = (data.subsidies || []).map(it => ({
          subsidyType: it.subsidyType,
          villageStreet: it.villageStreet,
          eventDate: it.eventDate,
          subsidyStandard: it.subsidyStandard || 0,
          startMonth: it.defaultStartMonth
        }))
      })
    },
    submitForm() {
      if (!this.form.subsidyPersonId || !this.subsidyRows.length) {
        this.$modal.msgWarning('请先查询身份证号并确认补贴信息')
        return
      }
      if (this.subsidyRows.some(it => !it.startMonth)) {
        this.$modal.msgWarning('请完善所有享受开始年月')
        return
      }
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const payload = {
          id: this.form.id,
          subsidyPersonId: this.form.subsidyPersonId,
          idCardNo: this.form.idCardNo,
          grantOrg: this.form.grantOrg,
          accountName: this.form.accountName,
          relationToInsured: this.form.relationToInsured,
          bankAccount: this.form.bankAccount,
          grantRemark: this.form.grantRemark,
          items: this.subsidyRows.map(it => ({
            subsidyType: it.subsidyType,
            startMonth: it.startMonth,
            villageStreet: it.villageStreet,
            eventDate: it.eventDate
          }))
        }
        saveBenefitDeterminationDraft(payload).then(res => {
          this.form.id = res.data || this.form.id
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleUploadAttachment() {
      if (!this.selectedAttachment || !this.form.id) {
        this.$modal.msgWarning('请先保存并选择ZIP文件')
        return
      }
      uploadBenefitAttachment(this.form.id, this.selectedAttachment).then(() => {
        this.$modal.msgSuccess('材料上传成功')
        return getBenefitDetermination(this.form.id)
      }).then(response => {
        const data = response.data || {}
        this.form = { ...this.form, materialStatus: data.materialStatus, materialImagePaths: data.materialImagePaths || [], materialZipPath: data.materialZipPath }
      })
    },
    handleSubmit(row) {
      this.$modal.confirm('确认提交该待遇核定进入复核吗？').then(() => submitBenefitDetermination(row.id)).then(() => {
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
      this.personInfo = {}
      this.socialInsurance = {}
      this.subsidyRows = []
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
    grantOrgLabel(val) {
      return selectDictLabel(this.dict.type.shebao_grant_org || [], val)
    },
    ynLabel(v) {
      if (v === '1' || v === 1) return '是'
      if (v === '0' || v === 0) return '否'
      return v || '-'
    },
    getSubsidyTypeLabel(subsidyType) {
      return {
        land_loss: '失地补贴',
        demolition: '拆迁补贴',
        village_official: '村干部补贴',
        land_loss_resident: '失地补贴',
        demolition_resident: '拆迁补贴'
      }[subsidyType] || subsidyType || '-'
    },
    formatSubsidyTypeMix(text) {
      if (!text) return '-'
      return text.split('/').map(this.getSubsidyTypeLabel).join(' / ')
    },
    formatStartMonth(row) {
      if (!row) return ''
      if (row.startMonth) return row.startMonth
      if (row.benefitStartYear && row.benefitStartMonth) {
        return `${row.benefitStartYear}-${String(row.benefitStartMonth).padStart(2, '0')}`
      }
      return ''
    },
    currentYm() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    },
    monthDiff(startYm, endYm) {
      if (!startYm || !endYm) return 0
      const [sy, sm] = startYm.split('-').map(n => parseInt(n))
      const [ey, em] = endYm.split('-').map(n => parseInt(n))
      const diff = (ey - sy) * 12 + (em - sm)
      return diff > 0 ? diff : 0
    },
    mulMoney(v, months) {
      const n = Number(v || 0)
      return (n * Number(months || 0)).toFixed(2)
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
.mt8 { margin-top: 8px; }
.mt12 { margin-top: 12px; }
.preview-list { display: flex; flex-wrap: wrap; }
.preview-item { margin-right: 12px; margin-bottom: 12px; }
.section-title { font-weight: 600; }
</style>
