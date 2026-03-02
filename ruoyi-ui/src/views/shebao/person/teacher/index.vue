<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item label="学校名称" prop="schoolName">
        <el-input v-model="queryParams.schoolName" placeholder="请输入学校名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['shebao:person:teacher:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="用户编号" prop="userCode" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" />
      <el-table-column label="学校名称" prop="schoolName" />
      <el-table-column label="任教年限" prop="teachingYears" />
      <el-table-column label="审批状态" align="center">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['shebao:person:teacher:edit']" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-upload" @click="handleSubmit(scope.row)" v-hasPermi="['shebao:person:teacher:submit']" v-if="scope.row.approvalStatus === 'draft' || scope.row.approvalStatus === 'rejected'">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改教龄补助登记对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <!-- 基础信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-user"></i>
            基础信息
            <el-tag v-if="form.personExists" type="success" size="mini">人员已存在</el-tag>
            <el-tag v-else type="info" size="mini">新增人员</el-tag>
          </div>
          <el-row>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCardNo">
                <el-input
                  v-model="form.idCardNo"
                  placeholder="请输入18位身份证号"
                  maxlength="18"
                  @input="handleIdCardInputChange"
                  @blur="handleIdCardNoBlurChange"
                  :disabled="isView"
                >
                  <template slot="suffix">
                    <i v-if="form.personExists" class="el-icon-success" style="color: #67c23a;" title="人员已存在"></i>
                    <i v-else class="el-icon-plus" style="color: #909399;" title="新增人员"></i>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名" maxlength="20" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="form.gender" :disabled="isView">
                  <el-radio label="1">男</el-radio>
                  <el-radio label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="生日" prop="birthday">
                <el-date-picker
                  v-model="form.birthday"
                  type="date"
                  placeholder="选择生日"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="户籍所在地" prop="householdRegistration">
                <el-input v-model="form.householdRegistration" placeholder="请输入户籍所在地" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="家庭住址" prop="homeAddress">
                <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属街道办" prop="streetOfficeId">
                <el-select v-model="form.streetOfficeId" placeholder="请选择街道办" style="width: 100%" @change="handleStreetChange" :disabled="isView">
                  <el-option v-for="item in streetOfficeList" :key="item.id" :label="item.streetName" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="所属村委会" prop="villageCommitteeId">
                <el-select v-model="form.villageCommitteeId" placeholder="请选择村委会" style="width: 100%" :disabled="isView">
                  <el-option v-for="item in villageCommitteeList" :key="item.id" :label="item.villageName" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否村合作组织成员" prop="isVillageCoopMember">
                <el-radio-group v-model="form.isVillageCoopMember" :disabled="isView">
                  <el-radio label="1">是</el-radio>
                  <el-radio label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="是否健在" prop="isAlive">
                <el-radio-group v-model="form.isAlive" :disabled="isView">
                  <el-radio label="1">是</el-radio>
                  <el-radio label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.isAlive === '0'">
              <el-form-item label="死亡时间" prop="deathDate">
                <el-date-picker
                  v-model="form.deathDate"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="选择死亡时间"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 教龄补助信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-reading"></i>
            教龄补助信息
          </div>
          <el-row>
            <el-col :span="12">
              <el-form-item label="学校名称" prop="schoolName">
                <el-input v-model="form.schoolName" placeholder="请输入学校名称" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="任教年限" prop="teachingYears">
                <el-input-number v-model="form.teachingYears" :min="0" :precision="0" style="width: 100%" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="!isView">确 定</el-button>
        <el-button @click="cancel">{{ isView ? '关 闭' : '取 消' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTeacherSubsidy, getTeacherSubsidy, addTeacherSubsidy, updateTeacherSubsidy, submitTeacherSubsidy, getTeacherSubsidyFormDataByIdCardNo } from '@/api/shebao/teacherSubsidy'
import { listStreetOffice } from '@/api/shebao/streetOffice'
import { listVillageCommittee } from '@/api/shebao/villageCommittee'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import { handleIdCardInput, handleIdCardBlur } from '@/utils/idCard'

export default {
  name: 'PersonTeacher',
  components: { ApprovalStatus },
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, idCardNo: null, schoolName: null },
      // 弹出层
      title: '',
      open: false,
      isView: false,
      streetOfficeList: [],
      villageCommitteeList: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        idCardNo: [
          { required: true, message: '身份证号不能为空', trigger: 'blur' },
          { pattern: /^[0-9]{17}[0-9Xx]$/, message: '请输入正确的18位身份证号', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '姓名不能为空', trigger: 'blur' },
          { max: 20, message: '姓名长度不能超过20个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthday: [
          { required: true, message: '请选择生日', trigger: 'change' }
        ],
        streetOfficeId: [
          { required: true, message: '请选择街道办', trigger: 'change' }
        ],
        villageCommitteeId: [
          { required: true, message: '请选择村委会', trigger: 'change' }
        ],
        schoolName: [
          { required: true, message: '学校名称不能为空', trigger: 'blur' }
        ],
        teachingYears: [
          { required: true, message: '任教年限不能为空', trigger: 'change' }
        ]
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
      listTeacherSubsidy(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
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
    // 获取街道办列表
    async getStreetOfficeList() {
      const rsp = await listStreetOffice()
      this.streetOfficeList = rsp.data || []
    },
    // 街道办变化
    async handleStreetChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeList = []
      if (!streetOfficeId) return
      const rsp = await listVillageCommittee({ streetOfficeId })
      this.villageCommitteeList = rsp.data || []
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
        subsidyPersonId: null,
        personExists: false,
        // 基础信息
        name: null,
        gender: null,
        idCardNo: null,
        birthday: null,
        householdRegistration: null,
        homeAddress: null,
        phone: null,
        isAlive: '1',
        deathDate: null,
        isVillageCoopMember: '0',
        streetOfficeId: null,
        villageCommitteeId: null,
        userCode: null,
        // 教龄补助信息
        schoolName: null,
        teachingYears: null,
        status: '0',
        remark: null
      }
      this.resetForm('form')
      this.isView = false
    },
    // 新增按钮操作
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增教龄补助登记'
    },
    // 修改按钮操作
    handleUpdate(row) {
      this.reset()
      getTeacherSubsidy(row.id).then(async response => {
        this.form = { ...this.form, ...(response.data || {}) }
        // 编辑时默认认为基础信息存在
        this.form.personExists = true
        await this.handleStreetChange(this.form.streetOfficeId)
        this.open = true
        this.title = '修改教龄补助登记'
      })
    },
    // 详情按钮操作
    handleView(row) {
      this.reset()
      this.isView = true
      getTeacherSubsidy(row.id).then(async response => {
        this.form = { ...this.form, ...(response.data || {}) }
        this.form.personExists = true
        await this.handleStreetChange(this.form.streetOfficeId)
        this.open = true
        this.title = '教龄补助登记详情'
      })
    },
    // 提交按钮
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTeacherSubsidy(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addTeacherSubsidy(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    // 提交审批
    handleSubmit(row) {
      this.$modal.confirm('是否确认提交？').then(() => {
        return submitTeacherSubsidy(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('提交成功')
      })
    },
    // 身份证号输入处理
    handleIdCardInputChange(value) {
      this.form.idCardNo = handleIdCardInput(value)
    },
    // 身份证号失焦处理 - 智能填充基础信息
    handleIdCardNoBlurChange() {
      if (!this.form.idCardNo) return

      const result = handleIdCardBlur(this.form.idCardNo, { showWarning: true })

      // 更新格式化后的身份证号码
      this.form.idCardNo = result.formattedIdCard

      // 如果校验失败,显示提示但不阻断逻辑
      if (result.formattedIdCard && !result.checkResult.valid) {
        this.$message.warning(result.checkResult.message)
      }

      // 自动填充生日和性别
      if (result.birthday) {
        this.form.birthday = result.birthday
      }
      if (result.gender !== null) {
        this.form.gender = result.gender
      }

      // 如果身份证格式正确,调用后端接口获取基础信息
      if (this.form.idCardNo && this.form.idCardNo.length === 18) {
        getTeacherSubsidyFormDataByIdCardNo(this.form.idCardNo).then(async response => {
          const data = response.data
          if (data.personExists) {
            // 基础信息存在,自动填充并标记
            this.form.personExists = true
            this.form.subsidyPersonId = data.subsidyPersonId
            this.form.name = data.name
            this.form.gender = data.gender
            this.form.birthday = data.birthday
            this.form.householdRegistration = data.householdRegistration
            this.form.homeAddress = data.homeAddress
            this.form.phone = data.phone
            this.form.streetOfficeId = data.streetOfficeId
            this.form.villageCommitteeId = data.villageCommitteeId
            this.form.isVillageCoopMember = data.isVillageCoopMember
            this.form.isAlive = data.isAlive
            this.form.deathDate = data.deathDate
            
            await this.handleStreetChange(data.streetOfficeId)
            
            this.$message.success('人员存在,已自动填充该人员的基础信息')
          } else {
            // 基础信息不存在,保留从身份证提取的信息
            this.form.personExists = false
            this.form.subsidyPersonId = null
          }
        }).catch(() => {
          this.form.personExists = false
          this.form.subsidyPersonId = null
        })
      }
    }
  }
}
</script>

<style scoped>
.form-section {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: #409eff;
}
</style>
