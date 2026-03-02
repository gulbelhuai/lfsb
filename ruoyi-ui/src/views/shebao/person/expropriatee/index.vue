<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item label="审批状态" prop="approvalStatus">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择" clearable>
          <el-option label="草稿" value="draft" />
          <el-option label="待复核" value="pending_review" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:person:expropriatee:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="用户编号" prop="userCode" width="120" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="征地批次" prop="expropriationBatch" width="120" />
      <el-table-column label="街道办事处" prop="streetOfficeName" width="120" />
      <el-table-column label="村委会" prop="villageCommitteeName" width="120" />
      <el-table-column label="审批状态" align="center" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">修改</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 表单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="征地批次" prop="expropriationBatch">
              <el-input v-model="form.expropriationBatch" placeholder="请输入征地批次" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="征地面积(亩)" prop="landArea">
              <el-input-number v-model="form.landArea" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="街道办事处" prop="streetOfficeId">
              <el-select v-model="form.streetOfficeId" placeholder="请选择" @change="handleStreetChange">
                <el-option v-for="item in streetOfficeList" :key="item.id" :label="item.streetName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="村委会" prop="villageCommitteeId">
              <el-select v-model="form.villageCommitteeId" placeholder="请选择">
                <el-option v-for="item in villageCommitteeList" :key="item.id" :label="item.villageName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPersonRegistration, addPersonRegistration, updatePersonRegistration, submitPersonRegistration } from '@/api/shebao/person'
import { listStreetOffice } from '@/api/shebao/streetOffice'
import { listVillageCommittee } from '@/api/shebao/villageCommittee'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

export default {
  name: 'PersonExpropriatee',
  components: { ApprovalStatus },
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      title: '',
      open: false,
      streetOfficeList: [],
      villageCommitteeList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        approvalStatus: null,
        subsidyType: 'expropriatee'
      },
      form: { subsidyType: 'expropriatee' },
      rules: {
        name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
        idCardNo: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }],
        streetOfficeId: [{ required: true, message: '请选择街道办事处', trigger: 'change' }],
        villageCommitteeId: [{ required: true, message: '请选择村委会', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
    this.getStreetOfficeList()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonRegistration(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getStreetOfficeList() {
      listStreetOffice().then(response => {
        this.streetOfficeList = response.data
      })
    },
    handleStreetChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeList = []
      if (streetOfficeId) {
        listVillageCommittee({ streetOfficeId }).then(response => {
          this.villageCommitteeList = response.data
        })
      }
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
      this.title = '新增被征地居民登记'
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.handleStreetChange(this.form.streetOfficeId)
      this.open = true
      this.title = '修改被征地居民登记'
    },
    handleView(row) {
      this.$router.push({ name: 'PersonDetail', params: { id: row.id } })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id) {
            updatePersonRegistration(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addPersonRegistration(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleSubmit(row) {
      this.$modal.confirm('是否确认提交？').then(() => {
        return submitPersonRegistration(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('提交成功')
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = { subsidyType: 'expropriatee' }
      this.resetForm('form')
    }
  }
}
</script>
