<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['shebao:historicalImport:import']"
        >导入</el-button>
      </el-col>
    </el-row>

    <el-table class="rx-table--compact" v-loading="loading" :data="batchList">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column label="补贴类型" align="center" prop="subsidyTypeLabel" width="140" />
      <el-table-column label="导入文件" align="center" prop="fileName" min-width="180" show-overflow-tooltip />
      <el-table-column label="导入行数" align="center" prop="totalRows" width="90" />
      <el-table-column label="成功行数" align="center" prop="successRows" width="90" />
      <el-table-column label="失败行数" align="center" prop="failureRows" width="90" />
      <el-table-column label="操作时间" align="center" prop="createTime" width="170" />
      <el-table-column label="操作人" align="center" prop="createBy" width="100" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.hasFailureFile"
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownloadFailure(scope.row)"
          >失败记录</el-button>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="历史数据导入" :visible.sync="importOpen" width="480px" append-to-body @close="resetImportDialog">
      <el-form label-width="90px" size="small">
        <el-form-item label="补贴类型">
          <el-select v-model="importForm.subsidyType" placeholder="请选择补贴类型" style="width: 100%" @change="handleSubsidyTypeChange">
            <el-option
              v-for="item in subsidyTypeOptions"
              :key="item.code"
              :label="item.label"
              :value="item.code"
              :disabled="!item.supported"
            >
              <span>{{ item.label }}</span>
              <span v-if="!item.supported" style="color: #909399; font-size: 12px; margin-left: 8px;">（尚未支持）</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="导入模板">
          <el-button
            type="text"
            icon="el-icon-download"
            :disabled="!currentTypeSupported"
            @click="downloadTemplate"
          >下载{{ currentTypeLabel }}导入模板</el-button>
          <div v-if="!currentTypeSupported" class="field-tip">该补贴类型历史导入功能尚未开放</div>
        </el-form-item>
        <el-form-item label="导入文件">
          <el-upload
            ref="upload"
            :limit="1"
            accept=".xlsx,.xls"
            :headers="upload.headers"
            :action="upload.url"
            :data="upload.data"
            :disabled="upload.isUploading || !currentTypeSupported"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-error="handleFileError"
            :auto-upload="false"
            drag
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">仅支持 xlsx / xls，请使用对应补贴类型的导入模板</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :disabled="!currentTypeSupported" :loading="upload.isUploading" @click="submitUpload">开始导入</el-button>
        <el-button @click="importOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { listHistoricalImport, listHistoricalImportSubsidyTypes } from '@/api/shebao/historicalImport'

export default {
  name: 'HistoricalDataImport',
  data() {
    return {
      loading: false,
      total: 0,
      batchList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      subsidyTypeOptions: [],
      importOpen: false,
      importForm: {
        subsidyType: 'land_loss_resident'
      },
      upload: {
        open: false,
        isUploading: false,
        headers: { Authorization: 'Bearer ' + getToken() },
        url: process.env.VUE_APP_BASE_API + '/shebao/historicalImport/importData',
        data: { subsidyType: 'land_loss_resident' }
      }
    }
  },
  computed: {
    currentType() {
      return this.subsidyTypeOptions.find(item => item.code === this.importForm.subsidyType)
    },
    currentTypeSupported() {
      return this.currentType ? this.currentType.supported : false
    },
    currentTypeLabel() {
      return this.currentType ? this.currentType.label : ''
    }
  },
  created() {
    this.loadSubsidyTypes()
    this.getList()
  },
  methods: {
    loadSubsidyTypes() {
      listHistoricalImportSubsidyTypes().then(res => {
        this.subsidyTypeOptions = res.data || []
        if (!this.importForm.subsidyType && this.subsidyTypeOptions.length > 0) {
          const firstSupported = this.subsidyTypeOptions.find(item => item.supported)
          this.importForm.subsidyType = firstSupported ? firstSupported.code : this.subsidyTypeOptions[0].code
          this.upload.data.subsidyType = this.importForm.subsidyType
        }
      })
    },
    getList() {
      this.loading = true
      listHistoricalImport(this.queryParams).then(response => {
        this.batchList = response.data.records || []
        this.total = response.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleImport() {
      this.importOpen = true
    },
    resetImportDialog() {
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
      this.upload.isUploading = false
    },
    handleSubsidyTypeChange(value) {
      this.upload.data.subsidyType = value
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
    },
    downloadTemplate() {
      if (!this.currentTypeSupported) {
        this.$modal.msgWarning('该补贴类型尚未支持历史数据导入')
        return
      }
      this.download('shebao/historicalImport/importTemplate', { subsidyType: this.importForm.subsidyType }, `历史数据录入模板_${this.currentTypeLabel}_${new Date().getTime()}.xlsx`)
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess(response) {
      this.upload.isUploading = false
      this.importOpen = false
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
      if (response.code === 200) {
        const data = response.data || {}
        this.$alert(
          `<div style="line-height: 1.8;">${response.msg || '导入完成'}<br/>成功：${data.successRows || 0} 行，失败：${data.failureRows || 0} 行</div>`,
          '导入结果',
          { dangerouslyUseHTMLString: true }
        )
        this.getList()
      } else {
        this.$modal.msgError(response.msg || '导入失败')
      }
    },
    handleFileError() {
      this.upload.isUploading = false
      this.$modal.msgError('导入失败，请检查文件格式或网络连接')
    },
    submitUpload() {
      if (!this.currentTypeSupported) {
        this.$modal.msgWarning('请先选择已支持的补贴类型')
        return
      }
      const files = this.$refs.upload && this.$refs.upload.uploadFiles
      if (!files || files.length === 0) {
        this.$modal.msgWarning('请先选择导入文件')
        return
      }
      this.$refs.upload.submit()
    },
    handleDownloadFailure(row) {
      const fileName = row.failureFileName || `导入失败记录_${row.subsidyTypeLabel}_${new Date().getTime()}.xlsx`
      this.download(`shebao/historicalImport/failureFile/${row.id}`, {}, fileName)
    }
  }
}
</script>

<style scoped>
.field-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
