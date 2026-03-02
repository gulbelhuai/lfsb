<template>
  <div class="app-container">
    <el-page-header @back="handleBack" content="教龄补助登记" />

    <el-card class="mt16">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" :disabled="isView">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input v-model="form.idCardNo" maxlength="18" placeholder="请输入18位身份证号">
                <el-button slot="append" icon="el-icon-search" @click="handleFillByIdCardNo" :disabled="!form.idCardNo">回填</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="1">男</el-radio>
                <el-radio label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker v-model="form.birthday" type="date" value-format="yyyy-MM-dd" placeholder="选择生日" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属街道办" prop="streetOfficeId">
              <el-select v-model="form.streetOfficeId" placeholder="请选择街道办" style="width: 100%" @change="handleStreetChange">
                <el-option v-for="item in streetOfficeList" :key="item.id" :label="item.streetName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属村委会" prop="villageCommitteeId">
              <el-select v-model="form.villageCommitteeId" placeholder="请选择村委会" style="width: 100%">
                <el-option v-for="item in villageCommitteeList" :key="item.id" :label="item.villageName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="户籍所在地" prop="householdRegistration">
              <el-input v-model="form.householdRegistration" placeholder="请输入户籍所在地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家庭住址" prop="homeAddress">
              <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="是否村合作组织成员" prop="isVillageCoopMember">
              <el-radio-group v-model="form.isVillageCoopMember">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="是否健在" prop="isAlive">
              <el-radio-group v-model="form.isAlive">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.isAlive === '0'">
            <el-form-item label="死亡时间" prop="deathDate">
              <el-date-picker v-model="form.deathDate" type="date" value-format="yyyy-MM-dd" placeholder="选择死亡时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">教龄补助信息</el-divider>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学校名称" prop="schoolName">
              <el-input v-model="form.schoolName" placeholder="请输入学校名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任教年限" prop="teachingYears">
              <el-input-number v-model="form.teachingYears" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>

        <div class="form-footer" v-if="!isView">
          <el-button type="primary" @click="submitForm">保 存</el-button>
          <el-button @click="handleBack">取 消</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { addTeacherSubsidy, getTeacherSubsidy, getTeacherSubsidyFormDataByIdCardNo, updateTeacherSubsidy } from '@/api/shebao/teacherSubsidy'
import { listStreetOffice } from '@/api/shebao/streetOffice'
import { listVillageCommittee } from '@/api/shebao/villageCommittee'

export default {
  name: 'PersonTeacherForm',
  data() {
    return {
      loading: false,
      streetOfficeList: [],
      villageCommitteeList: [],
      form: this.defaultForm(),
      rules: {
        idCardNo: [
          { required: true, message: '身份证号不能为空', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式错误', trigger: 'blur' }
        ],
        name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        birthday: [{ required: true, message: '请选择生日', trigger: 'change' }],
        streetOfficeId: [{ required: true, message: '请选择街道办', trigger: 'change' }],
        villageCommitteeId: [{ required: true, message: '请选择村委会', trigger: 'change' }],
        schoolName: [{ required: true, message: '学校名称不能为空', trigger: 'blur' }],
        teachingYears: [{ required: true, message: '任教年限不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    id() {
      return this.$route.params.id
    },
    isView() {
      return this.$route.name === 'PersonTeacherDetail'
    }
  },
  created() {
    this.getStreetOfficeList()
    if (this.id) {
      this.loadDetail(this.id)
    }
  },
  methods: {
    defaultForm() {
      return {
        id: null,
        subsidyPersonId: null,
        personExists: false,
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
        schoolName: null,
        teachingYears: null,
        status: '0',
        remark: null
      }
    },
    async getStreetOfficeList() {
      const rsp = await listStreetOffice()
      this.streetOfficeList = rsp.data || []
    },
    async handleStreetChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeList = []
      if (!streetOfficeId) return
      const rsp = await listVillageCommittee({ streetOfficeId })
      this.villageCommitteeList = rsp.data || []
    },
    async loadDetail(id) {
      const rsp = await getTeacherSubsidy(id)
      this.form = { ...this.defaultForm(), ...(rsp.data || {}) }
      // personExists：编辑/详情默认认为基础信息存在
      this.form.personExists = true
      await this.handleStreetChange(this.form.streetOfficeId)
    },
    async handleFillByIdCardNo() {
      if (!this.form.idCardNo) return
      const rsp = await getTeacherSubsidyFormDataByIdCardNo(this.form.idCardNo)
      const filled = rsp.data || {}
      // 保留已填写的教龄补助字段
      const keep = { schoolName: this.form.schoolName, teachingYears: this.form.teachingYears, remark: this.form.remark }
      this.form = { ...this.defaultForm(), ...filled, ...keep }
      await this.handleStreetChange(this.form.streetOfficeId)
    },
    submitForm() {
      this.$refs['form'].validate(async valid => {
        if (!valid) return
        if (this.form.id) {
          await updateTeacherSubsidy(this.form)
          this.$modal.msgSuccess('修改成功')
        } else {
          await addTeacherSubsidy(this.form)
          this.$modal.msgSuccess('新增成功')
        }
        this.handleBack()
      })
    },
    handleBack() {
      this.$tab.closePage()
    }
  }
}
</script>

<style scoped>
.mt16 { margin-top: 16px; }
.form-footer { margin-top: 16px; }
</style>

