<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审批状态" prop="approvalStatus">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择审批状态" clearable>
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

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['shebao:person:landloss:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['shebao:person:landloss:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['shebao:person:landloss:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:person:landloss:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="用户编号" align="center" prop="userCode" width="120" />
      <el-table-column label="姓名" align="center" prop="name" width="100" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="180" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <span>{{ scope.row.gender === '1' ? '男' : '女' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出生日期" align="center" prop="birthDate" width="120" />
      <el-table-column label="街道办事处" align="center" prop="streetOfficeName" width="120" />
      <el-table-column label="村委会" align="center" prop="villageCommitteeName" width="120" />
      <el-table-column label="审批状态" align="center" prop="approvalStatus" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['shebao:person:landloss:edit']"
            v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-upload"
            @click="handleSubmit(scope.row)"
            v-hasPermi="['shebao:person:landloss:submit']"
            v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'"
          >提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
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
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="选择出生日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="街道办事处" prop="streetOfficeId">
              <el-select v-model="form.streetOfficeId" placeholder="请选择街道办事处" @change="handleStreetChange">
                <el-option
                  v-for="item in streetOfficeList"
                  :key="item.id"
                  :label="item.streetName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="村委会" prop="villageCommitteeId">
              <el-select v-model="form.villageCommitteeId" placeholder="请选择村委会">
                <el-option
                  v-for="item in villageCommitteeList"
                  :key="item.id"
                  :label="item.villageName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="户籍地址" prop="householdAddress">
              <el-input v-model="form.householdAddress" placeholder="请输入户籍地址" />
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

    <!-- 详情对话框 -->
    <el-dialog title="失地居民详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户编号">{{ detailData.userCode }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === '1' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailData.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="街道办事处">{{ detailData.streetOfficeName }}</el-descriptions-item>
        <el-descriptions-item label="村委会">{{ detailData.villageCommitteeName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <approval-status :status="detailData.approvalStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审批历史 -->
      <el-divider content-position="left">审批历史</el-divider>
      <approval-history :history="approvalHistory" />
    </el-dialog>
  </div>
</template>

<script>
import { listPersonRegistration, getPersonRegistration, addPersonRegistration, updatePersonRegistration, delPersonRegistration, submitPersonRegistration } from '@/api/shebao/person'
import { listStreetOffice } from '@/api/shebao/streetOffice'
import { listVillageCommittee } from '@/api/shebao/villageCommittee'
import { getApprovalHistory } from '@/api/shebao/approval'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ApprovalHistory from '@/components/Shebao/ApprovalHistory'

export default {
  name: 'PersonLandLoss',
  components: {
    ApprovalStatus,
    ApprovalHistory
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 数据列表
      dataList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 审批历史
      approvalHistory: [],
      // 街道办事处列表
      streetOfficeList: [],
      // 村委会列表
      villageCommitteeList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        approvalStatus: null,
        subsidyType: 'land_loss_resident'
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '姓名不能为空', trigger: 'blur' }
        ],
        idCardNo: [
          { required: true, message: '身份证号不能为空', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式错误', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthDate: [
          { required: true, message: '请选择出生日期', trigger: 'change' }
        ],
        streetOfficeId: [
          { required: true, message: '请选择街道办事处', trigger: 'change' }
        ],
        villageCommitteeId: [
          { required: true, message: '请选择村委会', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getStreetOfficeList()
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true
      listPersonRegistration(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 获取街道办事处列表 */
    getStreetOfficeList() {
      listStreetOffice().then(response => {
        this.streetOfficeList = response.data
      })
    },
    /** 街道办事处改变时获取村委会列表 */
    handleStreetChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeList = []
      if (streetOfficeId) {
        listVillageCommittee({ streetOfficeId: streetOfficeId }).then(response => {
          this.villageCommitteeList = response.data
        })
      }
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        idCardNo: null,
        gender: null,
        birthDate: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        contactPhone: null,
        householdAddress: null,
        subsidyType: 'land_loss_resident',
        remark: null
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增失地居民登记'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getPersonRegistration(id).then(response => {
        this.form = response.data
        this.handleStreetChange(this.form.streetOfficeId)
        this.open = true
        this.title = '修改失地居民登记'
      })
    },
    /** 查看详情 */
    handleView(row) {
      this.detailData = row
      this.detailOpen = true
      // 获取审批历史
      getApprovalHistory('person_registration', row.id).then(response => {
        this.approvalHistory = response.data
      })
    },
    /** 提交表单 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePersonRegistration(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addPersonRegistration(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 提交审核 */
    handleSubmit(row) {
      const id = row.id
      this.$modal.confirm('是否确认提交该失地居民登记？').then(() => {
        return submitPersonRegistration(id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('提交成功')
      }).catch(() => {})
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除选中的失地居民登记记录？').then(() => {
        return delPersonRegistration(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/person/registration/export', {
        ...this.queryParams
      }, `person_registration_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
