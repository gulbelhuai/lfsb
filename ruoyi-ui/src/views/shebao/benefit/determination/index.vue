<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="请选择" clearable>
          <el-option label="失地居民" value="land_loss_resident" />
          <el-option label="被征地居民" value="expropriatee" />
          <el-option label="拆迁居民" value="demolition_resident" />
          <el-option label="村干部" value="village_official" />
          <el-option label="教龄补助" value="teacher" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:benefit:determination:add']">单个核定</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-upload" size="mini" @click="handleBatchImport" v-hasPermi="['shebao:benefit:determination:import']">批量导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subsidy_type" :value="scope.row.subsidyType"/>
        </template>
      </el-table-column>
      <el-table-column label="月补贴标准(元)" prop="monthlyAmount" width="120" />
      <el-table-column label="开始月份" prop="startMonth" width="100" />
      <el-table-column label="银行账号" prop="bankAccountNo" width="180" />
      <el-table-column label="审批状态" align="center" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" @click="handlePrint(scope.row)" v-hasPermi="['shebao:benefit:determination:print']">打印</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-if="scope.row.approvalStatus === 'draft'">修改</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.approvalStatus === 'draft'">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 核定对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入身份证号" @blur="handleSearchPerson">
                <el-button slot="append" icon="el-icon-search" @click="handleSearchPerson"></el-button>
              </el-input>
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
            <el-form-item label="补贴类型" prop="subsidyType">
              <el-select v-model="form.subsidyType" placeholder="请选择" disabled>
                <el-option label="失地居民" value="land_loss_resident" />
                <el-option label="被征地居民" value="expropriatee" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月补贴标准" prop="monthlyAmount">
              <el-input-number v-model="form.monthlyAmount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始月份" prop="startMonth">
              <el-date-picker v-model="form.startMonth" type="month" placeholder="选择月份" value-format="yyyy-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行名称" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入银行名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccountNo">
              <el-input v-model="form.bankAccountNo" placeholder="请输入银行账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户名" prop="bankAccountName">
              <el-input v-model="form.bankAccountName" placeholder="请输入账户名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="档案文件" prop="archiveFile">
              <el-upload
                class="upload-demo"
                action="/api/shebao/upload"
                :on-success="handleUploadSuccess"
                :file-list="fileList">
                <el-button size="small" type="primary">点击上传ZIP</el-button>
                <div slot="tip" class="el-upload__tip">只能上传zip文件</div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入" :visible.sync="importOpen" width="400px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/shebao/benefit/determination/batch"
        :on-success="handleImportSuccess"
        accept=".xlsx,.xls">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传Excel文件</div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import { listBenefitDetermination, addBenefitDetermination, submitBenefitDetermination } from '@/api/shebao/benefit'
import { searchResidents } from '@/api/shebao/person'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

export default {
  name: 'BenefitDetermination',
  dicts: ['subsidy_type'],
  components: { ApprovalStatus },
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      title: '',
      open: false,
      importOpen: false,
      fileList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        subsidyType: null
      },
      form: {},
      rules: {
        idCardNo: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }],
        monthlyAmount: [{ required: true, message: '月补贴标准不能为空', trigger: 'blur' }],
        startMonth: [{ required: true, message: '开始月份不能为空', trigger: 'change' }],
        bankName: [{ required: true, message: '银行名称不能为空', trigger: 'blur' }],
        bankAccountNo: [{ required: true, message: '银行账号不能为空', trigger: 'blur' }]
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
        this.dataList = (response.data && response.data.records) ? response.data.records : []
        this.total = (response.data && typeof response.data.total !== 'undefined') ? response.data.total : 0
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
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '待遇核定'
    },
    handleSearchPerson() {
      if (this.form.idCardNo) {
        searchResidents(this.form.idCardNo).then(response => {
          if (response.data && response.data.length > 0) {
            const person = response.data[0]
            this.form.personId = person.id
            this.form.name = person.name
            this.form.subsidyType = person.subsidyType
          } else {
            this.$modal.msgWarning('未找到该人员信息')
          }
        })
      }
    },
    handleBatchImport() {
      this.importOpen = true
    },
    handleUploadSuccess(response) {
      this.$modal.msgSuccess('上传成功')
      this.fileList.push(response.data)
    },
    handleImportSuccess(response) {
      this.$modal.msgSuccess('导入成功')
      this.importOpen = false
      this.getList()
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          addBenefitDetermination(this.form).then(() => {
            this.$modal.msgSuccess('核定成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleSubmit(row) {
      this.$modal.confirm('是否确认提交待遇核定？').then(() => {
        return submitBenefitDetermination(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('提交成功')
      })
    },
    handleView(row) {
      this.$router.push({ name: 'BenefitDetail', params: { id: row.id } })
    },
    handlePrint(row) {
      this.download(`shebao/benefit/determination/print/${row.id}`, {}, `待遇核定表_${row.id}_${new Date().getTime()}.xlsx`)
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {}
      this.fileList = []
      this.resetForm('form')
    }
  }
}
</script>
