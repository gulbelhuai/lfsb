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
      <el-form-item label="性别" prop="gender" v-if="false">
        <el-select v-model="queryParams.gender" placeholder="请选择性别" clearable>
          <el-option
            v-for="dict in dict.type.sys_user_sex"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系电话" prop="phone" v-if="false">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否健在" prop="isAlive" v-if="false">
        <el-select v-model="queryParams.isAlive" placeholder="请选择是否健在" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否村合作经济组织成员" prop="isVillageCoopMember" v-if="false">
        <el-select v-model="queryParams.isVillageCoopMember" placeholder="请选择是否村合作经济组织成员" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
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
          v-hasPermi="['shebao:subsidyPerson:add']"
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
          v-hasPermi="['shebao:subsidyPerson:edit']"
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
          v-hasPermi="['shebao:subsidyPerson:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-close"
          size="mini"
          @click="handleCancelRegistration"
          v-hasPermi="['shebao:subsidyPerson:cancel']"
        >人员注销登记</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:subsidyPerson:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:subsidyPerson:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="subsidyPersonList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="userCode" width="150" />
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="性别" align="center" prop="gender">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="生日" align="center" prop="birthday" width="120" />
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="是否健在" align="center" prop="isAlive" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isAlive === '1'" type="success">是</el-tag>
          <el-tag v-else type="danger">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否村合成员" align="center" prop="isVillageCoopMember" width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isVillageCoopMember === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属街道办" align="center" prop="streetOfficeName" width="120" />
      <el-table-column label="所属村委会" align="center" prop="villageCommitteeName" width="150" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['shebao:subsidyPerson:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:subsidyPerson:remove']"
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

    <!-- 添加或修改被补贴人基础信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input
                v-model="form.idCardNo"
                placeholder="请输入18位身份证号"
                maxlength="18"
                @input="handleIdCardInputChange"
                @blur="handleIdCardBlurChange"
              />
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
              <el-select v-model="form.streetOfficeId" placeholder="请选择所属街道办" @change="handleStreetOfficeChange">
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
              <el-select v-model="form.villageCommitteeId" placeholder="请选择所属村委会">
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
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="家庭住址" prop="homeAddress">
              <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否村合作经济组织成员" prop="isVillageCoopMember">
              <el-radio-group v-model="form.isVillageCoopMember">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否健在" prop="isAlive">
              <el-radio-group v-model="form.isAlive" @change="handleIsAliveChange">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="死亡时间" prop="deathDate" v-show="form.isAlive === '0'">
              <el-date-picker
                v-model="form.deathDate"
                type="date"
                placeholder="选择死亡时间"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
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

    <!-- 被补贴人导入对话框 -->
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
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的被补贴人数据
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

    <!-- 人员注销登记（死亡）对话框 -->
    <el-dialog title="人员注销登记" :visible.sync="cancelOpen" width="600px" append-to-body>
      <el-form ref="cancelForm" :model="cancelForm" :rules="cancelRules" label-width="120px">
        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="cancelForm.idCardNo" placeholder="请输入身份证号" maxlength="18" @blur="handleCancelIdCardBlur">
            <el-button slot="append" icon="el-icon-search" @click="handleCancelIdCardBlur"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="cancelPersonPreview.name" disabled />
        </el-form-item>
        <el-form-item label="用户编号">
          <el-input v-model="cancelPersonPreview.userCode" disabled />
        </el-form-item>
        <el-form-item label="死亡时间" prop="deathDate">
          <el-date-picker
            v-model="cancelForm.deathDate"
            type="date"
            placeholder="选择死亡时间"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="cancelForm.remark" type="textarea" placeholder="请输入备注（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitCancelForm">确 定</el-button>
        <el-button @click="cancelOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSubsidyPerson, getSubsidyPerson, delSubsidyPerson, addSubsidyPerson, updateSubsidyPerson, changeSubsidyPersonStatus, getSubsidyPersonByIdCardNo, cancelSubsidyPerson } from "@/api/shebao/subsidyPerson"
import { getToken } from "@/utils/auth"
import { getStreetOfficeSelectList } from "@/api/shebao/streetOffice"
import { getVillageCommitteeSelectList } from "@/api/shebao/villageCommittee"
import { handleIdCardInput, handleIdCardBlur, extractGenderFromIdCard, extractBirthdayFromIdCard } from "@/utils/idCard"

export default {
  name: "SubsidyPerson",
  components: {
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
      // 被补贴人基础信息表格数据
      subsidyPersonList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        gender: null,
        phone: null,
        householdRegistration: null,
        homeAddress: null,
        isAlive: null,
        isVillageCoopMember: null,
        streetOfficeId: null,
        villageCommitteeId: null,
        userCode: null,
        status: null
      },
      // 表单参数
      form: {},
      // 街道办选项
      streetOfficeOptions: [],
      // 村委会选项
      villageCommitteeOptions: [],
      // 表单校验
      rules: {
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" },
          { max: 20, message: "姓名长度不能超过20个字符", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /^[0-9]{17}[0-9Xx]$/, message: "请输入正确的18位身份证号", trigger: "blur" }
        ],
        birthday: [
          { required: true, message: "请选择生日", trigger: "change" }
        ],
        householdRegistration: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        homeAddress: [
          { required: true, message: "家庭住址不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^(1[3-9]\d{9}|\d{7,12})$/, message: "请输入正确的手机号或固话", trigger: "blur" }
        ],
        isAlive: [
          { required: true, message: "请选择是否健在", trigger: "change" }
        ],
        deathDate: [
          {
            validator: (rule, value, callback) => {
              if (this.form.isAlive === '0' && !value) {
                callback(new Error('死亡时间不能为空'))
              } else {
                callback()
              }
            },
            trigger: "change"
          }
        ],
        isVillageCoopMember: [
          { required: true, message: "请选择是否村合作经济组织成员", trigger: "change" }
        ],
        streetOfficeId: [
          { required: true, message: "请选择所属街道办", trigger: "change" }
        ],
        villageCommitteeId: [
          { required: true, message: "请选择所属村委会", trigger: "change" }
        ]
      },
      // 被补贴人导入参数
      upload: {
        // 是否显示弹出层（被补贴人导入）
        open: false,
        // 弹出层标题（被补贴人导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的被补贴人数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/shebao/subsidyPerson/importData"
      },
      // 注销登记弹窗
      cancelOpen: false,
      cancelForm: {
        idCardNo: null,
        deathDate: null,
        remark: null
      },
      cancelPersonPreview: {
        name: null,
        userCode: null,
        isAlive: null
      },
      cancelRules: {
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /^[0-9]{17}[0-9Xx]$/, message: "请输入正确的18位身份证号", trigger: "blur" }
        ],
        deathDate: [
          { required: true, message: "死亡时间不能为空", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.loadStreetOfficeOptions()
  },
  methods: {
    /** 查询被补贴人基础信息列表 */
    getList() {
      this.loading = true
      listSubsidyPerson(this.queryParams).then(response => {
        this.subsidyPersonList = response.data.records
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
        status: "0",
        remark: null
      }
      this.villageCommitteeOptions = []
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
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
      this.title = "添加被补贴人基础信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSubsidyPerson(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改被补贴人基础信息" + " - " + this.form.userCode
        // 加载村委会选项
        if (this.form.streetOfficeId) {
          this.loadVillageCommitteeOptions(this.form.streetOfficeId)
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSubsidyPerson(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSubsidyPerson(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除被补贴人编号为"' + ids + '"的数据项？').then(function() {
        return delSubsidyPerson(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/subsidyPerson/export', {
        ...this.queryParams
      }, `subsidy_person_${new Date().getTime()}.xlsx`)
    },
    /** 状态修改 */
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用"
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"被补贴人吗？').then(function() {
        return changeSubsidyPersonStatus(row.id, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + "成功")
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0"
      })
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "被补贴人基础信息导入"
      this.upload.open = true
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('shebao/subsidyPerson/importTemplate', {}, `subsidy_person_template_${new Date().getTime()}.xlsx`)
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

    /** 人员注销登记按钮 */
    handleCancelRegistration() {
      this.cancelOpen = true
      this.cancelForm = { idCardNo: null, deathDate: null, remark: null }
      this.cancelPersonPreview = { name: null, userCode: null, isAlive: null }
      this.$nextTick(() => {
        this.resetForm("cancelForm")
      })
    },

    /** 注销登记：身份证号失焦/查询 */
    handleCancelIdCardBlur() {
      if (!this.cancelForm.idCardNo) return
      // 注销登记需要查询所有人(包括已注销人员)以便验证和提示
      getSubsidyPersonByIdCardNo(this.cancelForm.idCardNo, true).then(res => {
        const p = res.data
        if (!p) {
          this.cancelPersonPreview = { name: null, userCode: null, isAlive: null }
          this.$modal.msgWarning("未找到该身份证号对应的人员")
          return
        }
        this.cancelPersonPreview = {
          name: p.name,
          userCode: p.userCode,
          isAlive: p.isAlive
        }
        if (p.isAlive === '0') {
          this.$message.warning("该人员已是死亡状态，如需更正请走信息修改流程")
        }
      })
    },

    /** 提交注销登记 */
    submitCancelForm() {
      this.$refs["cancelForm"].validate(valid => {
        if (!valid) return
        cancelSubsidyPerson(this.cancelForm).then(() => {
          this.$modal.msgSuccess("注销登记成功")
          this.cancelOpen = false
          this.getList()
        })
      })
    },

    /** 身份证号输入处理 */
    handleIdCardInputChange(value) {
      this.form.idCardNo = handleIdCardInput(value)
    },

    /** 身份证号失焦处理 */
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
    },

    /** 是否健在变化处理 */
    handleIsAliveChange(value) {
      if (value === '1') {
        this.form.deathDate = null
        // 清除死亡时间字段的验证错误
        this.$nextTick(() => {
          this.$refs.form.clearValidate('deathDate')
        })
      }
    },

    /** 街道办选择变化处理 */
    handleStreetOfficeChange(value) {
      this.form.villageCommitteeId = null
      this.villageCommitteeOptions = []
      if (value) {
        this.loadVillageCommitteeOptions(value)
      }
    },

    /** 加载街道办选项 */
    loadStreetOfficeOptions() {
      getStreetOfficeSelectList().then(response => {
        this.streetOfficeOptions = response.data
      })
    },

    /** 加载村委会选项 */
    loadVillageCommitteeOptions(streetOfficeId) {
      getVillageCommitteeSelectList(streetOfficeId).then(response => {
        this.villageCommitteeOptions = response.data
      })
    },
  }
}
</script>
