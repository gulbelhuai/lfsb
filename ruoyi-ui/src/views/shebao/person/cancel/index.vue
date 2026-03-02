<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="用户编号" prop="userCode">
        <el-input v-model="queryParams.userCode" placeholder="请输入用户编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:person:cancel:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['shebao:person:cancel:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['shebao:person:cancel:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['shebao:person:cancel:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="用户编号" prop="userCode" width="140" />
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="死亡时间" prop="deathDate" width="120" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['shebao:person:cancel:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['shebao:person:cancel:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['shebao:person:cancel:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="form.idCardNo" placeholder="请输入身份证号" maxlength="18" @blur="handleIdCardBlur">
            <el-button slot="append" icon="el-icon-search" @click="handleIdCardBlur" />
          </el-input>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="personPreview.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户编号">
              <el-input v-model="personPreview.userCode" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="死亡时间" prop="deathDate">
          <el-date-picker v-model="form.deathDate" type="date" value-format="yyyy-MM-dd" placeholder="选择死亡时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="注销登记详情" :visible.sync="detailOpen" width="650px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户编号">{{ detailData.userCode }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="死亡时间">{{ detailData.deathDate }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listPersonCancel, getPersonCancel, addPersonCancel, updatePersonCancel, delPersonCancel } from '@/api/shebao/personCancel'
import { getSubsidyPersonByIdCardNo } from '@/api/shebao/subsidyPerson'

export default {
  name: 'PersonCancel',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      ids: [],
      single: true,
      multiple: true,
      open: false,
      title: '',
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userCode: null,
        name: null,
        idCardNo: null
      },
      form: {},
      personPreview: { name: null, userCode: null, isAlive: null },
      rules: {
        idCardNo: [
          { required: true, message: '身份证号不能为空', trigger: 'blur' },
          { pattern: /^[0-9]{17}[0-9Xx]$/, message: '请输入正确的18位身份证号', trigger: 'blur' }
        ],
        deathDate: [{ required: true, message: '死亡时间不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonCancel(this.queryParams).then(res => {
        this.dataList = res.rows
        this.total = res.total
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    reset() {
      this.form = { id: null, idCardNo: null, deathDate: null, remark: null }
      this.personPreview = { name: null, userCode: null, isAlive: null }
      this.resetForm('form')
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增人员注销登记'
    },
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids[0]
      getPersonCancel(id).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        // 回显人员信息
        this.handleIdCardBlur()
        this.open = true
        this.title = '修改人员注销登记'
      })
    },
    handleView(row) {
      const id = row && row.id ? row.id : this.ids[0]
      getPersonCancel(id).then(res => {
        this.detailData = res.data || {}
        this.detailOpen = true
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids
      this.$modal.confirm('是否确认删除选中的注销登记记录？').then(() => {
        return delPersonCancel(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('shebao/personCancel/export', { ...this.queryParams }, `person_cancel_${new Date().getTime()}.xlsx`)
    },
    handleIdCardBlur() {
      if (!this.form.idCardNo) return
      // 注销登记需要查询所有人(包括已注销人员)以便验证和提示
      getSubsidyPersonByIdCardNo(this.form.idCardNo, true).then(res => {
        const p = res.data
        if (!p) {
          this.personPreview = { name: null, userCode: null, isAlive: null }
          this.$modal.msgWarning('未找到该身份证号对应的人员')
          return
        }
        this.personPreview = { name: p.name, userCode: p.userCode, isAlive: p.isAlive }
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) return
        const req = { ...this.form }
        const op = req.id ? updatePersonCancel(req) : addPersonCancel(req)
        op.then(() => {
          this.$modal.msgSuccess(req.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    }
  }
}
</script>

