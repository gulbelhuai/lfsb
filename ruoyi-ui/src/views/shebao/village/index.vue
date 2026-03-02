<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="村名" prop="villageName">
        <el-input
          v-model="queryParams.villageName"
          placeholder="请输入村名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="村编码" prop="villageCode">
        <el-input
          v-model="queryParams.villageCode"
          placeholder="请输入村编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="父级区划" prop="parentCode">
        <el-select v-model="queryParams.parentCode" placeholder="请选择父级区划" clearable>
          <el-option
            v-for="division in parentDivisionOptions"
            :key="division.divisionCode"
            :label="division.divisionName"
            :value="division.divisionCode"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="全路径" prop="fullName">
        <el-input
          v-model="queryParams.fullName"
          placeholder="请输入全路径名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="村级单位状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
          v-hasPermi="['shebao:village:add']"
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
          v-hasPermi="['shebao:village:edit']"
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
          v-hasPermi="['shebao:village:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:village:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:village:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="villageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="村名" align="center" prop="villageName" />
      <el-table-column label="村编码" align="center" prop="villageCode" />
      <el-table-column label="全路径名称" align="center" prop="fullName" width="300" />
      <el-table-column label="联系人" align="center" prop="contactPerson" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['shebao:village:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:village:remove']"
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

    <!-- 添加或修改村级单位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="村名" prop="villageName">
              <el-input v-model="form.villageName" placeholder="请输入村名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="村编码" prop="villageCode">
              <el-input v-model="form.villageCode" placeholder="请输入村编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="省份" prop="provinceCode">
              <el-select v-model="form.provinceCode" placeholder="请选择省份" @change="handleProvinceChange" clearable>
                <el-option
                  v-for="province in provinceOptions"
                  :key="province.divisionCode"
                  :label="province.divisionName"
                  :value="province.divisionCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="城市" prop="cityCode">
              <el-select v-model="form.cityCode" placeholder="请选择城市" @change="handleCityChange" clearable>
                <el-option
                  v-for="city in cityOptions"
                  :key="city.divisionCode"
                  :label="city.divisionName"
                  :value="city.divisionCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="县区" prop="countyCode">
              <el-select v-model="form.countyCode" placeholder="请选择县区" @change="handleCountyChange" clearable>
                <el-option
                  v-for="county in countyOptions"
                  :key="county.divisionCode"
                  :label="county.divisionName"
                  :value="county.divisionCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="乡镇" prop="parentCode">
              <el-select v-model="form.parentCode" placeholder="请选择乡镇" clearable>
                <el-option
                  v-for="township in townshipOptions"
                  :key="township.divisionCode"
                  :label="township.divisionName"
                  :value="township.divisionCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" />
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
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" type="textarea" placeholder="请输入详细地址" />
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

    <!-- 村级单位导入对话框 -->
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
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的村级单位数据
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
import { listVillage, getVillage, delVillage, addVillage, updateVillage, changeVillageStatus, getProvinces, getCities, getCounties, getTownships } from "@/api/shebao/village"
import { getToken } from "@/utils/auth"

export default {
  name: "Village",
  dicts: ['sys_normal_disable'],
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
      // 村级单位表格数据
      villageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        villageName: null,
        villageCode: null,
        parentCode: null,
        fullName: null,
        status: null
      },
      // 行政区划选项
      parentDivisionOptions: [],
      provinceOptions: [],
      cityOptions: [],
      countyOptions: [],
      townshipOptions: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        villageName: [
          { required: true, message: "村名不能为空", trigger: "blur" }
        ],
        villageCode: [
          { required: true, message: "村编码不能为空", trigger: "blur" }
        ],
        parentCode: [
          { required: true, message: "请选择归属乡镇", trigger: "change" }
        ]
      },
      // 村级单位导入参数
      upload: {
        // 是否显示弹出层（村级单位导入）
        open: false,
        // 弹出层标题（村级单位导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的村级单位数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/shebao/village/importData"
      }
    }
  },
  created() {
    this.getList()
    this.loadProvinceOptions()
    this.loadParentDivisionOptions()
  },
  methods: {
    /** 查询村级单位列表 */
    getList() {
      this.loading = true
      listVillage(this.queryParams).then(response => {
        this.villageList = response.data.records
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
        villageName: null,
        villageCode: null,
        parentCode: null,
        provinceCode: null,
        cityCode: null,
        countyCode: null,
        contactPerson: null,
        contactPhone: null,
        address: null,
        sortOrder: 0,
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
      this.title = "添加村级单位"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getVillage(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改村级单位"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateVillage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addVillage(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除村级单位编号为"' + ids + '"的数据项？').then(function() {
        return delVillage(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/village/export', {
        ...this.queryParams
      }, `village_${new Date().getTime()}.xlsx`)
    },
    /** 状态修改 */
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用"
      this.$modal.confirm('确认要"' + text + '""' + row.villageName + '"村级单位吗？').then(function() {
        return changeVillageStatus(row.id, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + "成功")
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0"
      })
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "村级单位导入"
      this.upload.open = true
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('shebao/village/importTemplate', {}, `village_template_${new Date().getTime()}.xlsx`)
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
    
    /** 加载省份选项 */
    loadProvinceOptions() {
      getProvinces().then(response => {
        this.provinceOptions = response.data
      })
    },
    
    /** 加载父级区划选项（用于查询） */
    loadParentDivisionOptions() {
      // 加载所有乡镇级行政区划用于查询筛选
      getTownships('').then(response => {
        this.parentDivisionOptions = response.data
      }).catch(() => {
        this.parentDivisionOptions = []
      })
    },
    
    /** 省份变化处理 */
    handleProvinceChange(provinceCode) {
      this.form.cityCode = null
      this.form.countyCode = null
      this.form.parentCode = null
      this.cityOptions = []
      this.countyOptions = []
      this.townshipOptions = []
      
      if (provinceCode) {
        getCities(provinceCode).then(response => {
          this.cityOptions = response.data
        })
      }
    },
    
    /** 城市变化处理 */
    handleCityChange(cityCode) {
      this.form.countyCode = null
      this.form.parentCode = null
      this.countyOptions = []
      this.townshipOptions = []
      
      if (cityCode) {
        getCounties(cityCode).then(response => {
          this.countyOptions = response.data
        })
      }
    },
    
    /** 县区变化处理 */
    handleCountyChange(countyCode) {
      this.form.parentCode = null
      this.townshipOptions = []
      
      if (countyCode) {
        getTownships(countyCode).then(response => {
          this.townshipOptions = response.data
        })
      }
    }
  }
}
</script>
