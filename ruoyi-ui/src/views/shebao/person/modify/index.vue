<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" placeholder="请输入姓名" clearable /></el-form-item>
      <el-form-item label="身份证号"><el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable /></el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择" clearable>
          <el-option label="草稿" value="draft" />
          <el-option label="待复核" value="pending_review" />
          <el-option label="待审批" value="pending_approve" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:person:modify:add']">新增申请</el-button>
      </el-col>
    </el-row>

    <el-table class="rx-table--compact" v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="户籍所在地" prop="householdRegistration" min-width="140" show-overflow-tooltip />
      <el-table-column label="所属街道办" prop="streetOfficeName" width="120" show-overflow-tooltip />
      <el-table-column label="所属村委会" prop="villageCommitteeName" width="120" show-overflow-tooltip />
      <el-table-column label="审批状态" align="center" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="320" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <template v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">
            <el-button size="mini" type="text" @click="handleEdit(scope.row)" v-hasPermi="['shebao:person:modify:edit']">编辑</el-button>
            <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-hasPermi="['shebao:person:modify:submit']">提交</el-button>
          </template>
          <template v-if="scope.row.approvalStatus === 'pending_review'">
            <el-button size="mini" type="success" @click="handleReview(scope.row, true)" v-hasPermi="['shebao:person:modify:review']">复核通过</el-button>
            <el-button size="mini" type="danger" @click="handleReview(scope.row, false)" v-hasPermi="['shebao:person:modify:review']">复核驳回</el-button>
          </template>
          <template v-if="scope.row.approvalStatus === 'pending_approve'">
            <el-button size="mini" type="success" @click="handleApprove(scope.row, true)" v-hasPermi="['shebao:person:modify:approve']">审批通过</el-button>
            <el-button size="mini" type="danger" @click="handleApprove(scope.row, false)" v-hasPermi="['shebao:person:modify:approve']">审批驳回</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑 关键信息修改 -->
    <el-dialog :title="formTitle" :visible.sync="formOpen" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="120px">
        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="form.idCardNo" placeholder="输入身份证号后回车查询人员" @keyup.enter.native="handleSearchPerson" :disabled="!!form.id">
            <el-button slot="append" icon="el-icon-search" @click="handleSearchPerson" :disabled="!!form.id">查询</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="户籍所在地" prop="householdRegistration">
          <el-input v-model="form.householdRegistration" placeholder="户籍所在地" />
        </el-form-item>
        <el-form-item label="所属街道办" prop="streetOfficeId">
          <el-select v-model="form.streetOfficeId" placeholder="请选择街道办" clearable filterable style="width:100%" @change="onStreetOfficeChange">
            <el-option v-for="item in streetOfficeOptions" :key="item.id" :label="item.streetName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属村委会" prop="villageCommitteeId">
          <el-select v-model="form.villageCommitteeId" placeholder="请先选街道办" clearable filterable style="width:100%" :disabled="!form.streetOfficeId">
            <el-option v-for="item in villageCommitteeOptions" :key="item.id" :label="item.villageName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm('draft')">保存草稿</el-button>
        <el-button type="success" @click="submitForm('submit')">保存并提交</el-button>
        <el-button @click="formOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 详情 -->
    <el-dialog title="关键信息修改详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border v-if="detailData.id">
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="户籍所在地">{{ detailData.householdRegistration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属街道办">{{ detailData.streetOfficeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属村委会">{{ detailData.villageCommitteeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批状态"><approval-status :status="detailData.approvalStatus" /></el-descriptions-item>
        <el-descriptions-item label="提交人">{{ detailData.submitBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.submitTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="复核人">{{ detailData.reviewBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="复核时间">{{ detailData.reviewTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approveBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ detailData.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="驳回原因" v-if="detailData.rejectReason">{{ detailData.rejectReason }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="detailOpen = false">关闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listPersonModify, getPersonModify, savePersonModify, submitPersonModify, reviewPersonModify, approvePersonModify } from '@/api/shebao/person'
import { getSubsidyPersonByIdCardNo } from '@/api/shebao/subsidyPerson'
import { getStreetOfficeSelectList } from '@/api/shebao/streetOffice'
import { getVillageCommitteeByStreetOffice } from '@/api/shebao/villageCommittee'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

export default {
  name: 'PersonModify',
  components: { ApprovalStatus },
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, idCardNo: null, approvalStatus: null },
      formOpen: false,
      formTitle: '新增关键信息修改',
      form: {
        id: null,
        subsidyPersonId: null,
        name: null,
        idCardNo: null,
        householdRegistration: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        remark: null
      },
      formRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        idCardNo: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
      },
      streetOfficeOptions: [],
      villageCommitteeOptions: [],
      detailOpen: false,
      detailData: {}
    }
  },
  created() {
    this.getList()
    this.loadStreetOfficeOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonModify(this.queryParams).then(response => {
        this.dataList = (response.data && response.data.records) ? response.data.records : []
        this.total = (response.data && response.data.total != null) ? response.data.total : 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.name = null
      this.queryParams.idCardNo = null
      this.queryParams.approvalStatus = null
      this.queryParams.pageNum = 1
      this.getList()
    },
    loadStreetOfficeOptions() {
      getStreetOfficeSelectList().then(res => {
        const d = res.data
        this.streetOfficeOptions = Array.isArray(d) ? d : (d && d.list ? d.list : [])
      }).catch(() => { this.streetOfficeOptions = [] })
    },
    onStreetOfficeChange(val) {
      this.form.villageCommitteeId = null
      this.villageCommitteeOptions = []
      if (!val) return Promise.resolve()
      return getVillageCommitteeByStreetOffice(val).then(res => {
        const d = res.data
        this.villageCommitteeOptions = Array.isArray(d) ? d : (d && d.list ? d.list : [])
      }).catch(() => { this.villageCommitteeOptions = []; return [] })
    },
    handleAdd() {
      this.resetForm()
      this.formTitle = '新增关键信息修改'
      this.formOpen = true
    },
    handleEdit(row) {
      getPersonModify(row.id).then(res => {
        const d = res.data || {}
        this.form = {
          id: d.id,
          subsidyPersonId: d.subsidyPersonId,
          name: d.name,
          idCardNo: d.idCardNo,
          householdRegistration: d.householdRegistration,
          streetOfficeId: d.streetOfficeId,
          villageCommitteeId: d.villageCommitteeId,
          remark: d.remark
        }
        if (d.streetOfficeId) this.onStreetOfficeChange(d.streetOfficeId)
        this.formTitle = '编辑关键信息修改'
        this.formOpen = true
      })
    },
    handleSearchPerson() {
      if (!this.form.idCardNo || !this.form.idCardNo.trim()) {
        this.$modal.msgWarning('请输入身份证号')
        return
      }
      getSubsidyPersonByIdCardNo(this.form.idCardNo.trim()).then(res => {
        const p = res.data
        if (!p) {
          this.$modal.msgWarning('未找到该人员')
          return
        }
        this.form.subsidyPersonId = p.id
        this.form.name = p.name
        this.form.idCardNo = p.idCardNo
        this.form.householdRegistration = p.householdRegistration || null
        this.form.streetOfficeId = p.streetOfficeId || null
        this.form.villageCommitteeId = p.villageCommitteeId || null
        if (p.streetOfficeId) this.onStreetOfficeChange(p.streetOfficeId)
      }).catch(() => this.$modal.msgError('查询人员失败'))
    },
    submitForm(action) {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        const data = {
          id: this.form.id,
          subsidyPersonId: this.form.subsidyPersonId,
          name: this.form.name,
          idCardNo: this.form.idCardNo,
          householdRegistration: this.form.householdRegistration,
          streetOfficeId: this.form.streetOfficeId,
          villageCommitteeId: this.form.villageCommitteeId,
          remark: this.form.remark
        }
        savePersonModify(data).then(res => {
          const id = (res.data !== undefined && res.data !== null) ? res.data : this.form.id
          if (action === 'submit' && id) {
            return submitPersonModify(id).then(() => {
              this.$modal.msgSuccess('提交成功')
              this.formOpen = false
              this.getList()
            })
          }
          this.$modal.msgSuccess('保存成功')
          this.formOpen = false
          this.getList()
        })
      })
    },
    handleSubmit(row) {
      this.$modal.confirm('确认提交该申请？提交后进入复核环节。').then(() => {
        return submitPersonModify(row.id)
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handleReview(row, approved) {
      const msg = approved ? '复核通过' : '复核驳回'
      this.$prompt(approved ? '复核意见（选填）' : '请输入驳回原因', msg, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: approved ? null : /.+/,
        inputErrorMessage: '驳回原因不能为空'
      }).then(({ value }) => {
        return reviewPersonModify(row.id, approved, value || '')
      }).then(() => {
        this.$modal.msgSuccess(approved ? '复核通过' : '已驳回')
        this.getList()
      }).catch(() => {})
    },
    handleApprove(row, approved) {
      const msg = approved ? '审批通过' : '审批驳回'
      this.$prompt(approved ? '审批意见（选填）' : '请输入驳回原因', msg, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: approved ? null : /.+/,
        inputErrorMessage: '驳回原因不能为空'
      }).then(({ value }) => {
        return approvePersonModify(row.id, approved, value || '')
      }).then(() => {
        this.$modal.msgSuccess(approved ? '审批通过' : '已驳回')
        this.getList()
      }).catch(() => {})
    },
    handleView(row) {
      getPersonModify(row.id).then(res => {
        this.detailData = res.data || {}
        this.detailOpen = true
      })
    },
    resetForm() {
      this.form = {
        id: null,
        subsidyPersonId: null,
        name: null,
        idCardNo: null,
        householdRegistration: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        remark: null
      }
      this.villageCommitteeOptions = []
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    }
  }
}
</script>

