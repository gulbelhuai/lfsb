<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="用户编号" prop="userCode">
        <el-input
          v-model="queryParams.userCode"
          placeholder="请输入用户编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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
      <el-form-item label="所属街道办" prop="streetOfficeId">
        <el-select v-model="queryParams.streetOfficeId" placeholder="请选择所属街道办" clearable @change="handleQueryStreetOfficeChange">
          <el-option
            v-for="item in streetOfficeOptions"
            :key="item.id"
            :label="item.streetName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属村委会" prop="villageCommitteeId">
        <el-select v-model="queryParams.villageCommitteeId" placeholder="请选择所属村委会" clearable>
          <el-option
            v-for="item in queryVillageCommitteeOptions"
            :key="item.id"
            :label="item.villageName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="approvalStatus">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择" clearable>
          <el-option label="草稿" value="draft" />
          <el-option label="待复核" value="pending_review" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="违法乱纪" prop="hasViolation">
        <el-select v-model="queryParams.hasViolation" placeholder="是否违法乱纪" clearable>
          <el-option label="否" value="0" />
          <el-option label="是" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['shebao:villageOfficial:add']"
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
          v-hasPermi="['shebao:villageOfficial:edit']"
        >修改</el-button>
      </el-col>
      <el-col v-show="false" :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['shebao:villageOfficial:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:villageOfficial:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:villageOfficial:import']"
        >导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      class="rx-table--compact"
      v-loading="loading"
      :data="villageOfficialList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="userCode" width="110" />
      <el-table-column label="姓名" align="center" prop="name" width="80" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="180" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="所属街道办" align="center" prop="streetOfficeName" />
      <el-table-column label="所属村委会" align="center" prop="villageName" />
      <el-table-column label="认定时所在村街" align="center" prop="villageStreet" width="120" show-overflow-tooltip />
      <el-table-column label="补贴标准(元)" align="center" prop="subsidyAmount" width="110" />
      <el-table-column label="违法乱纪" align="center" prop="hasViolation" width="90">
        <template slot-scope="scope">
          <dict-tag :options="[{label: '否', value: '0'}, {label: '是', value: '1'}]" :value="scope.row.hasViolation"/>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" prop="approvalStatus" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['shebao:villageOfficial:edit']"
          >修改</el-button>
          <el-button
            v-show="false"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:villageOfficial:remove']"
          >删除</el-button>
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

    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">

        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-user"></i>
            基础信息
            <el-tag v-if="form.personExists" type="success" size="mini">{{ form.userCode }}</el-tag>
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
                  @blur="handleIdCardBlurChange"
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
                <el-input v-model="form.name" placeholder="请输入姓名" maxlength="20" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="form.gender">
                  <el-radio
                    v-for="dict in dict.type.sys_user_sex"
                    :key="dict.value"
                    :label="dict.value"
                  >{{dict.label}}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="生日" prop="birthday" v-show="false">
                <el-date-picker
                  v-model="form.birthday"
                  type="date"
                  placeholder="选择生日"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="所属街道办" prop="streetOfficeId">
                <el-select v-model="form.streetOfficeId" placeholder="请选择街道办" @change="handleFormStreetOfficeChange">
                  <el-option
                    v-for="item in streetOfficeOptions"
                    :key="item.id"
                    :label="item.streetName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属村委会" prop="villageCommitteeId">
                <el-select v-model="form.villageCommitteeId" placeholder="请选择村委会">
                  <el-option
                    v-for="item in villageCommitteeOptions"
                    :key="item.id"
                    :label="item.villageName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="户籍所在地" prop="householdRegistration">
                <el-input v-model="form.householdRegistration" placeholder="请输入户籍所在地" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="家庭住址" prop="homeAddress">
                <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-postcard"></i>
            村干部任职信息
          </div>

          <el-row  style="margin-bottom: 22px;">
            <el-col :span="24">
              <div style="margin-bottom: 8px;">
                <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleAddPosition">添加任职</el-button>
              </div>
              <el-table
                :data="form.positionList"
                class="village-official-position-table"
                size="mini"
                style="width: 100%"
              >
                <el-table-column label="任职职位" align="center" width="160">
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.position"
                      size="mini"
                      placeholder="职位"
                      style="width: 100%"
                      @change="handlePositionFieldChange"
                    >
                      <el-option label="书记兼主任" value="1" />
                      <el-option label="书记或主任" value="2" />
                      <el-option label="村两委其他成员" value="3" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="上任时间" align="center" width="150">
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.startDate"
                      type="month"
                      size="mini"
                      placeholder="上任时间"
                      value-format="yyyy-MM"
                      style="width: 100%"
                      @change="handlePositionFieldChange"
                      @blur="handlePositionFieldChange"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="卸任时间" align="center" width="150">
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.endDate"
                      type="month"
                      size="mini"
                      placeholder="卸任时间"
                      value-format="yyyy-MM"
                      clearable
                      style="width: 100%"
                      @change="handlePositionFieldChange"
                      @blur="handlePositionFieldChange"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="任职年限" align="center" width="72">
                  <template slot-scope="scope">
                    <span>{{ formatPositionServiceYears(scope.row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="备注" align="center" min-width="150">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" size="mini" placeholder="备注" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="80">
                  <template slot-scope="scope">
                    <el-button
                      size="mini"
                      type="text"
                      icon="el-icon-delete"
                      @click="handleDeletePosition(scope.$index)"
                    >删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="村干部补贴标准" prop="subsidyAmount">
                <el-input
                  :value="form.subsidyAmount == null ? '' : Number(form.subsidyAmount).toFixed(2)"
                  placeholder="自动计算"
                  readonly
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否违法乱纪" prop="hasViolation">
                <el-radio-group v-model="form.hasViolation">
                  <el-radio label="0">否</el-radio>
                  <el-radio label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="认定时所在村街" prop="villageStreet">
                <el-input v-model="form.villageStreet" placeholder="请输入认定时所在村街" />
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
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的村干部数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVillageOfficial, getVillageOfficial, delVillageOfficial, addVillageOfficial, updateVillageOfficial, getFormDataByIdCardNo, calculateVillageOfficialBenefit } from "@/api/shebao/villageOfficial"
import { getToken } from "@/utils/auth"
import { getStreetOfficeSelectList } from "@/api/shebao/streetOffice"
import { getVillageCommitteeByStreetOffice } from "@/api/shebao/villageCommittee"
import { handleIdCardInput, handleIdCardBlur } from "@/utils/idCard"
import ApprovalStatus from "@/components/Shebao/ApprovalStatus"

export default {
  name: "VillageOfficial",
  components: { ApprovalStatus },
  dicts: ["sys_user_sex"],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      villageOfficialList: [],
      streetOfficeOptions: [],
      villageCommitteeOptions: [],
      queryVillageCommitteeOptions: [],
      title: "",
      open: false,
      calculatingBenefit: false,
      benefitPromise: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userCode: null,
        name: null,
        idCardNo: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        approvalStatus: null,
        hasViolation: null
      },
      form: {},
      rules: {
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /^[0-9]{17}[0-9Xx]$/, message: "请输入正确的18位身份证号", trigger: "blur" }
        ],
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" },
          { max: 20, message: "姓名长度不能超过20个字符", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        birthday: [
          { required: true, message: "请选择生日", trigger: "change" }
        ],
        householdRegistration: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        homeAddress: [
          { max: 255, message: "家庭住址长度不能超过255个字符", trigger: "blur" }
        ],
        phone: [
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
                return
              }
              if (!/^(1[3-9]\d{9}|\d{7,12})$/.test(value)) {
                callback(new Error("请输入正确的手机号或固话"))
              } else {
                callback()
              }
            },
            trigger: "blur"
          }
        ],
        streetOfficeId: [
          { required: true, message: "请选择所属街道办", trigger: "change" }
        ],
        villageCommitteeId: [
          { required: true, message: "请选择所属村委会", trigger: "change" }
        ],
        hasViolation: [
          { required: true, message: "请选择是否违法乱纪或判刑", trigger: "change" }
        ]
      },
      upload: {
        open: false,
        title: "",
        isUploading: false,
        updateSupport: 0,
        headers: { Authorization: "Bearer " + getToken() },
        url: process.env.VUE_APP_BASE_API + "/shebao/villageOfficial/importData"
      }
    }
  },
  created() {
    this.getStreetOfficeOptions()
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listVillageOfficial(this.queryParams).then(response => {
        this.villageOfficialList = response.data.records
        this.total = response.data.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
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
        isAlive: "1",
        deathDate: null,
        isVillageCoopMember: "1",
        streetOfficeId: null,
        villageCommitteeId: null,
        userCode: null,
        subsidyAmount: null,
        hasViolation: "0",
        villageStreet: null,
        remark: null,
        positionList: []
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryVillageCommitteeOptions = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加村干部信息"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getVillageOfficial(id).then(response => {
        this.handleFormStreetOfficeChange(response.data.streetOfficeId)
        this.form = response.data
        if (!this.form.positionList) {
          this.form.positionList = []
        }
        this.form.positionList = this.form.positionList.map(item => ({
          ...item,
          startDate: this.toYearMonthStr(item.startDate),
          endDate: this.toYearMonthStr(item.endDate)
        }))
        this.open = true
        this.title = "修改村干部信息"
      })
    },
    submitForm() {
      if (!this.validatePositionListRequired(true)) {
        return
      }
      this.recalculateByBackend().then(() => {
        this.$refs["form"].validate(valid => {
          if (valid) {
            const req = {
              ...this.form,
              positionList: (this.form.positionList || []).map(row => ({
                ...row,
                startDate: this.toFirstDayStr(row.startDate),
                endDate: this.toFirstDayStr(row.endDate)
              }))
            }
            if (this.form.id != null) {
              updateVillageOfficial(req).then(() => {
                this.$modal.msgSuccess("修改成功，已进入待复核")
                this.open = false
                this.getList()
              })
            } else {
              addVillageOfficial(req).then(() => {
                this.$modal.msgSuccess("新增成功，已进入待复核")
                this.open = false
                this.getList()
              })
            }
          }
        })
      })
    },
    validatePositionListRequired(showMessage = false) {
      const rows = this.form.positionList || []
      for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        if (!row.position || !row.startDate || !row.endDate) {
          if (showMessage) {
            this.$message.warning(`第${i + 1}行任职信息：任职职位、上任时间、卸任时间均不能为空`)
          }
          return false
        }
      }
      return true
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除村干部编号为"' + ids + '"的数据项？').then(function() {
        return delVillageOfficial(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download("shebao/villageOfficial/export", {
        ...this.queryParams
      }, `village_official_${new Date().getTime()}.xlsx`)
    },
    handleImport() {
      this.upload.title = "村干部信息导入"
      this.upload.open = true
    },
    importTemplate() {
      this.download("shebao/villageOfficial/importTemplate", {}, `village_official_template_${new Date().getTime()}.xlsx`)
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
      this.getList()
    },
    submitFileForm() {
      this.$refs.upload.submit()
    },
    handleIdCardInputChange(value) {
      this.form.idCardNo = handleIdCardInput(value)
      if (!this.form.idCardNo || this.form.idCardNo.length < 18) {
        this.form.userCode = null
        this.form.subsidyPersonId = null
        this.form.personExists = false
      }
    },
    handleIdCardBlurChange() {
      if (!this.form.idCardNo) return

      const result = handleIdCardBlur(this.form.idCardNo, { showWarning: true })
      this.form.idCardNo = result.formattedIdCard

      if (result.formattedIdCard && !result.checkResult.valid) {
        this.$message.warning(result.checkResult.message)
      }

      if (result.birthday) {
        this.form.birthday = result.birthday
      }
      if (result.gender !== null) {
        this.form.gender = result.gender
      }

      if (this.form.idCardNo && this.form.idCardNo.length === 18) {
        getFormDataByIdCardNo(this.form.idCardNo).then(response => {
          const data = response.data
          if (data.personExists) {
            this.handleFormStreetOfficeChange(data.streetOfficeId)
            this.form.personExists = true
            this.form.subsidyPersonId = data.subsidyPersonId
            this.form.name = data.name
            this.form.gender = data.gender
            this.form.birthday = data.birthday
            this.form.householdRegistration = data.householdRegistration
            this.form.homeAddress = data.homeAddress
            this.form.phone = data.phone
            this.form.isAlive = data.isAlive
            this.form.deathDate = data.deathDate
            this.form.isVillageCoopMember = data.isVillageCoopMember
            this.form.streetOfficeId = data.streetOfficeId
            this.form.villageCommitteeId = data.villageCommitteeId
            this.form.userCode = data.userCode
            this.$message.success("人员存在，已自动填充该人员的基础信息")
          } else {
            this.form.personExists = false
            this.form.subsidyPersonId = null
            this.form.userCode = null
          }
        }).catch(() => {
          this.form.personExists = false
          this.form.subsidyPersonId = null
          this.form.userCode = null
        })
      } else {
        this.form.userCode = null
        this.form.subsidyPersonId = null
        this.form.personExists = false
      }
    },
    getStreetOfficeOptions() {
      getStreetOfficeSelectList().then(response => {
        this.streetOfficeOptions = response.data
      })
    },
    handleQueryStreetOfficeChange(streetOfficeId) {
      this.queryParams.villageCommitteeId = null
      this.queryVillageCommitteeOptions = []
      if (streetOfficeId) {
        getVillageCommitteeByStreetOffice(streetOfficeId).then(response => {
          this.queryVillageCommitteeOptions = response.data
        })
      }
    },
    handleFormStreetOfficeChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeOptions = []
      if (streetOfficeId) {
        getVillageCommitteeByStreetOffice(streetOfficeId).then(response => {
          this.villageCommitteeOptions = response.data
        })
      }
    },
    handleAddPosition() {
      this.form.positionList.push({
        position: null,
        startDate: null,
        endDate: null,
        serviceYears: null,
        status: "0",
        remark: null
      })
      this.recalculateByBackend()
    },
    handleDeletePosition(index) {
      this.form.positionList.splice(index, 1)
      this.recalculateByBackend()
    },
    toYearMonthStr(v) {
      if (!v) return null
      const s = String(v)
      return s.length >= 7 ? s.slice(0, 7) : s
    },
    toFirstDayStr(v) {
      if (!v) return null
      const s = String(v)
      if (/^\d{4}-\d{2}$/.test(s)) return `${s}-01`
      if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return `${s.slice(0, 7)}-01`
      return s
    },
    formatPositionServiceYears(row) {
      if (row.serviceYears === null || row.serviceYears === undefined || row.serviceYears === "") return "—"
      return Number(row.serviceYears).toFixed(2)
    },
    handlePositionFieldChange() {
      this.recalculateByBackend()
    },
    recalculateByBackend() {
      if (!this.validatePositionListRequired(false)) {
        return Promise.resolve()
      }
      if (this.calculatingBenefit) {
        return this.benefitPromise || Promise.resolve()
      }
      const req = {
        ...this.form,
        positionList: (this.form.positionList || []).map(row => ({
          ...row,
          startDate: this.toFirstDayStr(row.startDate),
          endDate: this.toFirstDayStr(row.endDate)
        }))
      }
      this.calculatingBenefit = true
      this.benefitPromise = calculateVillageOfficialBenefit(req).then(response => {
        const data = response.data || {}
        this.form.subsidyAmount = data.subsidyAmount
        this.form.positionList = data.positionList || this.form.positionList
      }).finally(() => {
        this.calculatingBenefit = false
        this.benefitPromise = null
      })
      return this.benefitPromise
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

/* 表格内日期/下拉默认约 220px，列窄时右边框被裁切；强制铺满单元格 */
.village-official-position-table >>> .el-date-editor.el-input,
.village-official-position-table >>> .el-date-editor.el-input__inner {
  width: 100% !important;
}
</style>
