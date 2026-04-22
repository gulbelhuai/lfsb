<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table class="rx-table--compact" v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="身份证号" prop="idCardNo" min-width="180" />
      <el-table-column label="补贴类型" min-width="180">
        <template slot-scope="scope">
          <span>{{ formatSubsidyTypeMix(scope.row.subsidyType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发放机构" min-width="140">
        <template slot-scope="scope">{{ grantOrgLabel(scope.row.grantOrg) }}</template>
      </el-table-column>
      <el-table-column label="开户名" prop="accountName" min-width="120" />
      <el-table-column label="与参保人关系" prop="relationToInsured" min-width="130" />
      <el-table-column label="银行账号" prop="bankAccount" min-width="180" />
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleModify(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="修改发放信息" :visible.sync="modifyOpen" width="900px">
      <el-card shadow="never" class="mb8" v-if="detailData.id">
        <div slot="header" class="section-title">人员基本信息</div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="姓名">{{ detailData.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailData.idCardNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="补贴类型">{{ formatSubsidyTypeMix(detailData.subsidyType) }}</el-descriptions-item>
          <el-descriptions-item label="审批状态">{{ approvalStatusLabel(detailData.approvalStatus) }}</el-descriptions-item>
          <el-descriptions-item label="材料状态">{{ materialStatusLabel(detailData.materialStatus) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-form ref="modifyFormRef" :model="modifyForm" :rules="rules" label-width="120px">
        <el-card shadow="never" class="mb8">
          <div slot="header" class="section-title">发放方式（可修改）</div>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="发放机构" prop="grantOrg">
                <el-select v-model="modifyForm.grantOrg" style="width: 100%" placeholder="请选择">
                  <el-option v-for="d in dict.type.shebao_grant_org" :key="d.value" :label="d.label" :value="d.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开户名" prop="accountName">
                <el-input v-model="modifyForm.accountName" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="与参保人关系" prop="relationToInsured">
                <el-input v-model="modifyForm.relationToInsured" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="银行账号" prop="bankAccount">
                <el-input v-model="modifyForm.bankAccount" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注">
            <el-input type="textarea" :rows="2" v-model="modifyForm.grantRemark" />
          </el-form-item>
        </el-card>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="submitLoading" @click="submitModify">保 存</el-button>
        <el-button @click="modifyOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBenefitDetermination, listBenefitDetermination, updateBenefitDetermination } from '@/api/shebao/benefit'
import { selectDictLabel } from '@/utils/ruoyi'

export default {
  name: 'BenefitModify',
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: false,
      submitLoading: false,
      total: 0,
      dataList: [],
      modifyOpen: false,
      detailData: {},
      modifyForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        approvalStatus: 'approved'
      },
      rules: {
        grantOrg: [{ required: true, message: '发放机构不能为空', trigger: 'change' }],
        accountName: [{ required: true, message: '开户名不能为空', trigger: 'blur' }],
        relationToInsured: [{ required: true, message: '与参保人关系不能为空', trigger: 'blur' }],
        bankAccount: [{ required: true, message: '银行账号不能为空', trigger: 'blur' }]
      }
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
      this.queryParams.approvalStatus = 'approved'
      this.handleQuery()
    },
    handleModify(row) {
      getBenefitDetermination(row.id).then(response => {
        const data = response.data || {}
        if (data.approvalStatus !== 'approved') {
          this.$modal.msgWarning('仅审核通过的待遇核定记录允许修改发放信息')
          return
        }
        this.detailData = data
        this.modifyForm = {
          id: data.id,
          grantOrg: data.grantOrg,
          accountName: data.accountName,
          relationToInsured: data.relationToInsured || '本人',
          bankAccount: data.bankAccount,
          grantRemark: data.grantRemark
        }
        this.modifyOpen = true
      })
    },
    submitModify() {
      this.$refs.modifyFormRef.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        updateBenefitDetermination({
          id: this.modifyForm.id,
          grantOrg: this.modifyForm.grantOrg,
          accountName: this.modifyForm.accountName,
          relationToInsured: this.modifyForm.relationToInsured,
          bankAccount: this.modifyForm.bankAccount,
          grantRemark: this.modifyForm.grantRemark
        }).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.modifyOpen = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    },
    grantOrgLabel(val) {
      return selectDictLabel(this.dict.type.shebao_grant_org || [], val) || '-'
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
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
.section-title { font-weight: 600; }
</style>
