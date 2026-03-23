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
        <el-select v-model="queryParams.streetOfficeId" placeholder="请选择所属街道办" clearable @change="handleStreetOfficeChange">
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
            v-for="item in villageCommitteeOptions"
            :key="item.id"
            :label="item.villageName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="征地时间" v-if="false">
        <el-date-picker
          v-model="landRequisitionTimeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleLandRequisitionTimeChange"
        />
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
          v-hasPermi="['shebao:landLossResident:add']"
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
          v-hasPermi="['shebao:landLossResident:edit']"
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
          v-hasPermi="['shebao:landLossResident:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:landLossResident:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:landLossResident:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      class="rx-table--compact"
      v-loading="loading"
      :data="landLossResidentList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="userCode" width="110" />
      <el-table-column label="姓名" align="center" prop="name" width="80" />
      <el-table-column label="身份证" align="center" prop="idCardNo" width="180" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <!-- <el-table-column label="生日" align="center" prop="birthday" width="120" /> -->
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="参保状态" align="center" prop="subsidyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.shebao_subsidy_status" :value="scope.row.subsidyStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="人员状态" align="center" prop="personStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.shebao_person_status" :value="scope.row.personStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="所属街道办" align="center" prop="streetOfficeName" />
      <el-table-column label="所属村委会" align="center" prop="villageCommitteeName" />
      <el-table-column label="征地时间" align="center" prop="landRequisitionTime" width="120" />
      <el-table-column label="认定时间" align="center" prop="recognitionTime" width="120" />
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
            v-hasPermi="['shebao:landLossResident:edit']"
          >修改</el-button>
          <el-button
            v-show="false"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:landLossResident:remove']"
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

    <!-- 添加或修改失地居民信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">

        <!-- 基础信息区域 -->
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
                    <i v-if="form.personExists" class="el-icon-success" style="color: #67c23a;" title="form.userCode"></i>
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
              <el-form-item label="生日" prop="birthday">
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
                <el-select v-model="form.streetOfficeId" placeholder="请选择街道办" @change="handleStreetOfficeChange">
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
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>


        <!-- 失地居民特有信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-date"></i>
            失地居民信息
          </div>

          <el-row>
            <el-col :span="12">
              <el-form-item label="征地时间" prop="landRequisitionTime">
                <el-date-picker
                  v-model="form.landRequisitionTime"
                  type="date"
                  placeholder="选择征地时间"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="完成补偿时间" prop="compensationCompleteTime">
                <el-date-picker
                  v-model="form.compensationCompleteTime"
                  type="date"
                  placeholder="选择完成补偿时间"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="认定为失地居民时间" prop="recognitionTime">
                <el-date-picker
                  v-model="form.recognitionTime"
                  type="date"
                  placeholder="选择认定时间"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="征地批次" prop="landRequisitionBatch">
                <el-input v-model="form.landRequisitionBatch" placeholder="请输入征地批次" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="认定时所在村街" prop="villageStreet">
                <el-input v-model="form.villageStreet" placeholder="请输入认定时所在村街" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否村合作经济组织成员" prop="isVillageCoopMember">
                <el-radio-group v-model="form.isVillageCoopMember">
                  <el-radio label="1">是</el-radio>
                  <el-radio label="0">否</el-radio>
                </el-radio-group>
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

    <!-- 失地居民导入对话框 -->
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
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的失地居民数据
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
import { listLandLossResident, getLandLossResident, delLandLossResident, addLandLossResident, updateLandLossResident, getFormDataByIdCardNo } from "@/api/shebao/landLossResident"
import { getToken } from "@/utils/auth"
import { getStreetOfficeSelectList } from "@/api/shebao/streetOffice"
import { getVillageCommitteeByStreetOffice } from "@/api/shebao/villageCommittee"
import { handleIdCardInput, handleIdCardBlur } from "@/utils/idCard"
import ApprovalStatus from "@/components/Shebao/ApprovalStatus"

export default {
  name: "LandLossResident",
  dicts: ['sys_normal_disable', 'sys_user_sex', 'shebao_subsidy_status', 'shebao_person_status'],
  components: { ApprovalStatus },
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
      // 失地居民信息表格数据
      landLossResidentList: [],
      // 街道办选项
      streetOfficeOptions: [],
      // 村委会选项
      villageCommitteeOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 征地时间范围
      landRequisitionTimeRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        landRequisitionTimeStart: null,
        landRequisitionTimeEnd: null,
        recognitionTimeStart: null,
        recognitionTimeEnd: null,
        userCode: null
      },
      // 表单参数
      form: {},
      // 表单校验
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
        streetOfficeId: [
          { required: true, message: "请选择所属街道办", trigger: "change" }
        ],
        villageCommitteeId: [
          { required: true, message: "请选择所属村委会", trigger: "change" }
        ]
      },
      // 失地居民导入参数
      upload: {
        // 是否显示弹出层（失地居民导入）
        open: false,
        // 弹出层标题（失地居民导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的失地居民数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/shebao/landLossResident/importData"
      }
    }
  },
  created() {
    this.getStreetOfficeOptions()
    this.getList()
  },
  methods: {
    /** 查询失地居民信息列表 */
    getList() {
      this.loading = true
      listLandLossResident(this.queryParams).then(response => {
        this.landLossResidentList = response.data.records
        this.total = response.data.total
        this.loading = false
      })
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
        isAlive: "1",
        deathDate: null,
        isVillageCoopMember: "1",
        streetOfficeId: null,
        villageCommitteeId: null,
        userCode: null,
        // 失地居民信息
        landRequisitionTime: null,
        compensationCompleteTime: null,
        recognitionTime: null,
        remark: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.landRequisitionTimeRange = []
      this.queryParams.landRequisitionTimeStart = null
      this.queryParams.landRequisitionTimeEnd = null
      this.resetForm("queryForm")
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
      this.title = "添加失地居民信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getLandLossResident(id).then(response => {
        this.handleStreetOfficeChange(response.data.streetOfficeId)
        this.form = response.data
        this.open = true
        this.title = "修改失地居民信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLandLossResident(this.form).then(response => {
              this.$modal.msgSuccess(response.msg || "修改并提交复核成功")
              this.open = false
              this.getList()
            })
          } else {
            addLandLossResident(this.form).then(response => {
              this.$modal.msgSuccess(response.msg || "登记并提交复核成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除失地居民编号为"' + ids + '"的数据项？').then(function() {
        return delLandLossResident(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/landLossResident/export', {
        ...this.queryParams
      }, `land_loss_resident_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "失地居民信息导入"
      this.upload.open = true
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('shebao/landLossResident/importTemplate', {}, `land_loss_resident_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
      this.getList()
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit()
    },

    /** 身份证号输入处理 */
    handleIdCardInputChange(value) {
      this.form.idCardNo = handleIdCardInput(value)
    },

    /** 身份证号失焦处理 - 智能填充基础信息 */
    handleIdCardBlurChange() {
      if (!this.form.idCardNo) return

      const result = handleIdCardBlur(this.form.idCardNo, { showWarning: true })

      // 更新格式化后的身份证号码
      this.form.idCardNo = result.formattedIdCard

      // 如果校验失败，显示提示但不阻断逻辑
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

      // 如果身份证格式正确，调用后端接口获取基础信息
      if (this.form.idCardNo && this.form.idCardNo.length === 18) {
        getFormDataByIdCardNo(this.form.idCardNo).then(response => {
          const data = response.data
          if (data.personExists) {
            // 基础信息存在，自动填充并标记
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

            this.$message.success('人员存在，已自动填充该人员的基础信息')
          } else {
            // 基础信息不存在，保留从身份证提取的信息
            this.form.personExists = false
            this.form.subsidyPersonId = null
          }
        }).catch(() => {
          this.form.personExists = false
          this.form.subsidyPersonId = null
        })
      }
    },


    /** 征地时间范围变化处理 */
    handleLandRequisitionTimeChange(timeRange) {
      if (timeRange && timeRange.length === 2) {
        this.queryParams.landRequisitionTimeStart = timeRange[0]
        this.queryParams.landRequisitionTimeEnd = timeRange[1]
      } else {
        this.queryParams.landRequisitionTimeStart = null
        this.queryParams.landRequisitionTimeEnd = null
      }
    },

    /** 获取街道办选项 */
    getStreetOfficeOptions() {
      getStreetOfficeSelectList().then(response => {
        this.streetOfficeOptions = response.data
      })
    },

    /** 街道办选择变化处理 */
    handleStreetOfficeChange(streetOfficeId) {
      this.form.villageCommitteeId = null
      this.villageCommitteeOptions = []
      if (streetOfficeId) {
        getVillageCommitteeByStreetOffice(streetOfficeId).then(response => {
          this.villageCommitteeOptions = response.data
        })
      }
    },

    /** 是否健在选择变化处理 */
    handleIsAliveChange(value) {
      if (value === '1') {
        this.form.deathDate = null
        // 清除死亡时间字段的验证错误
        this.$nextTick(() => {
          this.$refs.form.clearValidate('deathDate')
        })
      }
    },
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
