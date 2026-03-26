<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
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
      <el-form-item label="征地批次" prop="landRequisitionBatch">
        <el-input
          v-model="queryParams.landRequisitionBatch"
          placeholder="请输入征地批次"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="基准日">
        <el-date-picker
          v-model="baseDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleBaseDateChange"
        />
      </el-form-item>
      <el-form-item label="所属村委会" prop="villageCode">
        <DivisionSelector
          v-model="queryParams.villageCode"
          mode="select"
          scenario="village"
          placeholder="请选择所属村委会"
          filterable
          clearable
        />
      </el-form-item>
      <el-form-item label="城乡居保" prop="joinUrbanRuralInsurance">
        <el-select v-model="queryParams.joinUrbanRuralInsurance" placeholder="选择参加城乡居保" clearable>
          <el-option label="否" value="0" />
          <el-option label="是" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="职工养老" prop="joinEmployeePension">
        <el-select v-model="queryParams.joinEmployeePension" placeholder="选择参加职工养老" clearable>
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
          v-hasPermi="['shebao:expropriateeSubsidy:add']"
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
          v-hasPermi="['shebao:expropriateeSubsidy:edit']"
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
          v-hasPermi="['shebao:expropriateeSubsidy:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:expropriateeSubsidy:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:expropriateeSubsidy:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      class="rx-table--compact"
      v-loading="loading"
      :data="expropriateeSubsidyList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="180" />
      <el-table-column label="征地批次" align="center" prop="landRequisitionBatch" width="120" />
      <el-table-column label="基准日" align="center" prop="baseDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.baseDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="基准日年龄" align="center" prop="ageAtBaseDate" width="100" />
      <el-table-column label="补贴年限" align="center" prop="subsidyYears" width="100" />
      <el-table-column label="补贴金额" align="center" prop="subsidyAmount" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.subsidyAmount ? scope.row.subsidyAmount.toFixed(2) : '0.00' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="城乡居保" align="center" prop="joinUrbanRuralInsurance" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.joinUrbanRuralInsurance === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="职工养老" align="center" prop="joinEmployeePension" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.joinEmployeePension === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['shebao:expropriateeSubsidy:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:expropriateeSubsidy:remove']"
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

    <!-- 添加或修改被征地参保补贴对话框 -->
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
            <el-col :span="8">
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
            <el-col :span="8">
              <el-form-item label="生日" prop="birthday">
                <el-date-picker
                  v-model="form.birthday"
                  type="date"
                  placeholder="选择生日"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                  @change="calculateAge"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="籍贯" prop="nativePlace">
                <el-input v-model="form.nativePlace" placeholder="请输入籍贯" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属村委会" prop="villageCode">
                <DivisionSelector
                  ref="villageSelector"
                  v-model="form.villageCode"
                  mode="select"
                  scenario="village"
                  placeholder="请选择所属村委会"
                  filterable
                  clearable
                  @change="handleVillageChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24">
              <el-form-item label="家庭住址" prop="homeAddress">
                <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 被征地参保补贴特有信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <i class="el-icon-money"></i>
            被征地参保补贴信息
          </div>

          <el-row>
            <el-col :span="12">
              <el-form-item label="征地批次" prop="landRequisitionBatch">
                <el-input v-model="form.landRequisitionBatch" placeholder="请输入征地批次" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="基准日" prop="baseDate">
                <el-date-picker
                  v-model="form.baseDate"
                  type="date"
                  placeholder="选择基准日"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                  @change="calculateAge"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="职工养老月数" prop="employeePensionMonths">
                <el-input-number v-model="form.employeePensionMonths" :min="0" :max="999" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="灵活就业月数" prop="flexibleEmploymentMonths">
                <el-input-number v-model="form.flexibleEmploymentMonths" :min="0" :max="999" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="困难补贴年限" prop="difficultySubsidyYears">
                <el-input-number v-model="form.difficultySubsidyYears" :min="0" :max="99.99" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="截至基准日年龄" prop="ageAtBaseDate">
                <el-input-number v-model="form.ageAtBaseDate" :min="0" :max="120" controls-position="right" style="width: 100%" readonly />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="补贴年限" prop="subsidyYears">
                <el-input-number v-model="form.subsidyYears" :min="0" :max="99.99" :precision="2" controls-position="right" style="width: 100%" readonly />
                <div class="field-tip">系统自动计算，不可修改</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="补贴金额" prop="subsidyAmount">
                <el-input-number v-model="form.subsidyAmount" :min="0" :max="999999.99" :precision="2" controls-position="right" style="width: 100%" readonly />
                <div class="field-tip">系统自动计算，不可修改</div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="选择参加城乡居保" prop="joinUrbanRuralInsurance">
                <el-radio-group v-model="form.joinUrbanRuralInsurance">
                  <el-radio label="0">否</el-radio>
                  <el-radio label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择参加职工养老" prop="joinEmployeePension">
                <el-radio-group v-model="form.joinEmployeePension">
                  <el-radio label="0">否</el-radio>
                  <el-radio label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="职工养老保险" prop="hasEmployeePension">
                <el-radio-group v-model="form.hasEmployeePension">
                  <el-radio label="0">未领取</el-radio>
                  <el-radio label="1">已领取</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-radio-group v-model="form.status">
                  <el-radio
                    v-for="dict in dict.type.sys_normal_disable"
                    :key="dict.value"
                    :label="dict.value"
                  >{{dict.label}}</el-radio>
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

    <!-- 被征地参保补贴导入对话框 -->
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
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的被征地参保补贴数据
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
import { listExpropriateeSubsidy, getExpropriateeSubsidy, delExpropriateeSubsidy, addExpropriateeSubsidy, updateExpropriateeSubsidy, getFormDataByIdCardNo, calculateSubsidy } from "@/api/shebao/expropriateeSubsidy"
import { getToken } from "@/utils/auth"
import DivisionSelector from "@/components/DivisionSelector"
import { handleIdCardInput, handleIdCardBlur } from "@/utils/idCard"

export default {
  name: "ExpropriateeSubsidy",
  components: {
    DivisionSelector
  },
  dicts: ['sys_normal_disable', 'sys_user_sex'],
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
      // 被征地参保补贴表格数据
      expropriateeSubsidyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 基准日范围
      baseDateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        landRequisitionBatch: null,
        baseDateStart: null,
        baseDateEnd: null,
        villageCode: null,
        joinUrbanRuralInsurance: null,
        joinEmployeePension: null,
        status: null
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
        nativePlace: [
          { required: true, message: "籍贯不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^(1[3-9]\d{9}|\d{7,12})$/, message: "请输入正确的手机号或固话", trigger: "blur" }
        ],
        homeAddress: [
          { required: true, message: "家庭住址不能为空", trigger: "blur" }
        ],
        villageCode: [
          { required: true, message: "请选择所属村委会", trigger: "change" }
        ],
        hasEmployeePension: [
          { required: true, message: "请选择是否已领取职工养老保险待遇", trigger: "change" }
        ]
      },
      // 被征地参保补贴导入参数
      upload: {
        // 是否显示弹出层（被征地参保补贴导入）
        open: false,
        // 弹出层标题（被征地参保补贴导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的被征地参保补贴数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/shebao/expropriateeSubsidy/importData"
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询被征地参保补贴列表 */
    getList() {
      this.loading = true
      listExpropriateeSubsidy(this.queryParams).then(response => {
        this.expropriateeSubsidyList = response.data.records
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
        nativePlace: null,
        phone: null,
        homeAddress: null,
        villageCode: null,
        villageName: null,
        householdNo: null,
        hasEmployeePension: "0",
        // 被征地参保补贴信息
        landRequisitionBatch: null,
        baseDate: null,
        employeePensionMonths: 0,
        flexibleEmploymentMonths: 0,
        difficultySubsidyYears: 0,
        ageAtBaseDate: null,
        subsidyYears: 0,
        subsidyAmount: 0,
        joinUrbanRuralInsurance: "0",
        joinEmployeePension: "0",
        status: "0",
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
      this.baseDateRange = []
      this.queryParams.baseDateStart = null
      this.queryParams.baseDateEnd = null
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
      this.title = "添加被征地参保补贴"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row && row.id != null ? row.id : (Array.isArray(this.ids) ? this.ids[0] : this.ids)
      getExpropriateeSubsidy(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改被征地参保补贴"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateExpropriateeSubsidy(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addExpropriateeSubsidy(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
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
      this.$modal.confirm('是否确认删除被征地参保补贴编号为"' + ids + '"的数据项？').then(function() {
        return delExpropriateeSubsidy(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/expropriateeSubsidy/export', {
        ...this.queryParams
      }, `expropriatee_subsidy_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "被征地参保补贴导入"
      this.upload.open = true
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('shebao/expropriateeSubsidy/importTemplate', {}, `expropriatee_subsidy_template_${new Date().getTime()}.xlsx`)
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
            this.form.nativePlace = data.nativePlace
            this.form.phone = data.phone
            this.form.homeAddress = data.homeAddress
            this.form.villageCode = data.villageCode
            this.form.villageName = data.villageName
            this.form.householdNo = data.householdNo
            this.form.hasEmployeePension = data.hasEmployeePension

            // 重新计算年龄
            this.calculateAge()

            this.$message.success('人员存在，已自动填充该人员的基础信息')
          } else {
            // 基础信息不存在，保留从身份证提取的信息
            this.form.personExists = false
            this.form.subsidyPersonId = null

            // 重新计算年龄
            this.calculateAge()
          }
        }).catch(() => {
          this.form.personExists = false
          this.form.subsidyPersonId = null
        })
      }
    },

    /** 村委会选择变化处理 */
    handleVillageChange(villageCode, villageData) {
      if (villageData) {
        this.form.villageName = villageData.divisionName
      }
    },

    /** 基准日范围变化处理 */
    handleBaseDateChange(timeRange) {
      if (timeRange && timeRange.length === 2) {
        this.queryParams.baseDateStart = timeRange[0]
        this.queryParams.baseDateEnd = timeRange[1]
      } else {
        this.queryParams.baseDateStart = null
        this.queryParams.baseDateEnd = null
      }
    },

    /** 计算截至基准日的年龄、补贴年限和补贴金额 */
    calculateAge() {
      if (this.form.birthday && this.form.baseDate) {
        // 调用后端接口进行计算
        const params = {
          birthday: this.form.birthday,
          baseDate: this.form.baseDate
        }

        calculateSubsidy(params).then(response => {
          if (response.code === 200) {
            this.form.ageAtBaseDate = response.data.ageAtBaseDate
            this.form.subsidyYears = response.data.subsidyYears
            this.form.subsidyAmount = response.data.subsidyAmount
          }
        }).catch(error => {
          console.error('计算补贴数据失败:', error)
          // 如果后端计算失败，使用前端简单计算作为备选
          this.calculateAgeLocal()
        })
      } else {
        // 如果生日或基准日为空，清空相关字段
        this.form.ageAtBaseDate = null
        this.form.subsidyYears = 0
        this.form.subsidyAmount = 0
      }
    },

    /** 本地计算年龄（备选方案） */
    calculateAgeLocal() {
      if (this.form.birthday && this.form.baseDate) {
        const birthday = new Date(this.form.birthday)
        const baseDate = new Date(this.form.baseDate)
        const age = baseDate.getFullYear() - birthday.getFullYear()
        const monthDiff = baseDate.getMonth() - birthday.getMonth()

        if (monthDiff < 0 || (monthDiff === 0 && baseDate.getDate() < birthday.getDate())) {
          this.form.ageAtBaseDate = age - 1
        } else {
          this.form.ageAtBaseDate = age
        }

        // 清空补贴相关字段，提示用户刷新
        this.form.subsidyYears = 0
        this.form.subsidyAmount = 0
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

.field-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.2;
}
</style>
