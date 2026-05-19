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
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="请选择补贴类型" clearable>
          <el-option label="失地居民" value="land_loss_resident" />
          <el-option label="被征地农民" value="expropriatee" />
          <el-option label="拆迁居民" value="demolition_resident" />
          <el-option label="村干部" value="village_official" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table class="rx-table--compact" v-loading="loading" :data="dataList">
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="用户编号" align="center" prop="userCode" width="120" />
      <el-table-column label="姓名" align="center" prop="name" width="100" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" align="center" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <span>{{ getSubsidyTypeLabel(scope.row.subsidyType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="街道办事处" align="center" prop="streetOfficeName" width="120" />
      <el-table-column label="村委会" align="center" prop="villageCommitteeName" width="120" />
      <el-table-column label="提交时间" align="center" prop="submitTime" width="160" />
      <el-table-column label="操作" align="center" width="250">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="success"
            @click="handlePass(scope.row)"
            v-hasPermi="['shebao:person:review:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleReject(scope.row)"
            v-hasPermi="['shebao:person:review:reject']"
          >不通过</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="1100px" append-to-body>
      <el-divider content-position="left">人员基础信息</el-divider>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="用户编号" :span="2">{{ detailData.userCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ detailData.idCardNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === '1' ? '男' : detailData.gender === '2' ? '女' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailData.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="街道办事处">{{ detailData.streetOfficeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="村委会" :span="2">{{ detailData.villageCommitteeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="户籍所在地" :span="2">{{ detailData.householdRegistration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ detailData.homeAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">{{ getSubsidyTypeLabel(detailSubsidyType) }}登记信息</el-divider>

      <div v-if="detailSubsidyType === 'land_loss_resident' && landLossRows.length > 0">
        <el-table class="rx-table--compact" :data="landLossRows" border size="small">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="认定时间" prop="recognitionTime" align="center" />
          <el-table-column label="征地时间" prop="landRequisitionTime" align="center" />
          <el-table-column label="完成补偿时间" prop="compensationCompleteTime" align="center" />
          <el-table-column label="征地批次" prop="landRequisitionBatch" align="center" />
          <el-table-column label="认定时所在村街" prop="villageStreet" align="center" />
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" align="center" width="170" />
        </el-table>
      </div>

      <div v-else-if="detailSubsidyType === 'expropriatee' && expropriateeRows.length > 0">
        <el-table class="rx-table--compact" :data="expropriateeRows" border size="small">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="征地批次" prop="landRequisitionBatch" align="center" />
          <el-table-column label="征地时所在村街" prop="villageStreet" align="center" />
          <el-table-column label="基准日" prop="baseDate" align="center" />
          <el-table-column label="职工养老月数" prop="employeePensionMonths" align="center" />
          <el-table-column label="灵活就业月数" prop="flexibleEmploymentMonths" align="center" />
          <el-table-column label="困难补贴月数" prop="difficultySubsidyMonths" align="center" />
          <el-table-column label="基准日年龄" prop="ageAtBaseDate" align="center" />
          <el-table-column label="补贴年限" prop="subsidyYears" align="center" />
          <el-table-column label="补贴金额" prop="subsidyAmount" align="center" />
          <el-table-column label="参加城乡居保" prop="joinUrbanRuralInsurance" align="center">
            <template slot-scope="scope">
              {{ scope.row.joinUrbanRuralInsurance === '1' ? '是' : scope.row.joinUrbanRuralInsurance === '0' ? '否' : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="参加职工养老" prop="joinEmployeePension" align="center">
            <template slot-scope="scope">
              {{ scope.row.joinEmployeePension === '1' ? '是' : scope.row.joinEmployeePension === '0' ? '否' : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="已领职工养老待遇" prop="hasEmployeePension" align="center">
            <template slot-scope="scope">
              {{ scope.row.hasEmployeePension === '1' ? '是' : scope.row.hasEmployeePension === '0' ? '否' : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" align="center" width="170" />
        </el-table>
      </div>

      <div v-else-if="detailSubsidyType === 'demolition_resident' && demolitionRows.length > 0">
        <el-table class="rx-table--compact" :data="demolitionRows" border size="small">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="拆迁事由" prop="demolitionReason" align="center" />
          <el-table-column label="拆迁时间" prop="demolitionTime" align="center" />
          <el-table-column label="认定时间" prop="recognitionTime" align="center" />
          <el-table-column label="认定时所在村街" prop="villageStreet" align="center" />
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" align="center" width="170" />
        </el-table>
      </div>

      <div v-else-if="detailSubsidyType === 'village_official' && villageOfficialRows.length > 0">
        <el-table class="rx-table--compact" :data="villageOfficialRows" border size="small">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="累计任职年限" prop="totalServiceYears" align="center" />
          <el-table-column label="补贴标准(元)" prop="subsidyAmount" align="center" />
          <el-table-column label="是否违法乱纪或判刑" prop="hasViolation" align="center">
            <template slot-scope="scope">
              {{ scope.row.hasViolation === '1' ? '是' : scope.row.hasViolation === '0' ? '否' : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="认定时所在村街" prop="villageStreet" align="center" />
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" align="center" width="170" />
        </el-table>
      </div>

      <div v-else style="text-align: center; color: #909399; padding: 12px 0 20px;">
        暂无该补贴登记信息
      </div>

      <!-- 审批历史 -->
      <el-divider content-position="left">审批历史</el-divider>
      <approval-history :history="approvalHistory" />

      <!-- 复核操作 -->
      <div slot="footer" class="dialog-footer">
        <el-button type="success" @click="handlePass(reviewContext)" v-hasPermi="['shebao:person:review:approve']">通过</el-button>
        <el-button type="danger" @click="handleReject(reviewContext)" v-hasPermi="['shebao:person:review:reject']">不通过</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 复核对话框 -->
    <el-dialog :title="reviewTitle" :visible.sync="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewForm" :model="reviewForm" :rules="reviewRules" label-width="100px">
        <el-form-item :label="reviewType === 'reject' ? '不通过原因' : '复核意见'" prop="remark">
          <el-input v-model="reviewForm.remark" type="textarea" :rows="4" :placeholder="reviewType === 'reject' ? '请输入不通过原因' : '请输入复核意见'" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReview">确认</el-button>
        <el-button @click="reviewOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPersonReview, getPersonReviewDetail, reviewPersonPass, reviewPersonReject } from '@/api/shebao/person'
import { getApprovalHistory } from '@/api/shebao/approval'
import ApprovalHistory from '@/components/Shebao/ApprovalHistory'

const EMPTY_SUBSIDY_INFO = {
  landLossResidents: [],
  expropriateeSubsidies: [],
  demolitionResidents: [],
  villageOfficials: []
}

export default {
  name: 'PersonReview',
  dicts: ['subsidy_type'],
  components: {
    ApprovalHistory
  },
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      detailOpen: false,
      detailTitle: '人员登记复核详情',
      detailSubsidyType: null,
      detailData: {},
      reviewContext: null,
      subsidyInfo: { ...EMPTY_SUBSIDY_INFO },
      approvalHistory: [],
      reviewOpen: false,
      reviewTitle: '',
      reviewType: '',
      currentRow: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        subsidyType: null,
        approvalStatus: 'pending_review'
      },
      reviewForm: {
        remark: null
      },
      reviewRules: {
        remark: [
          { required: true, message: '请输入不通过原因', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    landLossRows() {
      return this.subsidyInfo.landLossResidents || []
    },
    expropriateeRows() {
      return this.subsidyInfo.expropriateeSubsidies || []
    },
    demolitionRows() {
      return this.subsidyInfo.demolitionResidents || []
    },
    villageOfficialRows() {
      return this.subsidyInfo.villageOfficials || []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonReview(this.queryParams).then(response => {
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
    handleView(row) {
      this.reviewContext = row
      this.detailSubsidyType = row.subsidyType
      this.detailTitle = `人员登记复核详情 - ${this.getSubsidyTypeLabel(row.subsidyType)}`
      getPersonReviewDetail(row.subsidyType, row.id).then(response => {
        const data = response.data || {}
        this.detailData = data.residentInfo || {}
        this.subsidyInfo = data.subsidyInfo || { ...EMPTY_SUBSIDY_INFO }
        this.detailOpen = true
      })
      getApprovalHistory('person_register', row.id).then(response => {
        this.approvalHistory = response.data
      })
    },
    handlePass(row) {
      this.$confirm('是否确认通过', '系统提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return reviewPersonPass(row.subsidyType, row.id, '')
      }).then(() => {
        this.$modal.msgSuccess('复核通过')
        this.detailOpen = false
        this.getList()
      }).catch(() => {})
    },
    handleReject(row) {
      this.currentRow = row
      this.reviewType = 'reject'
      this.reviewTitle = '复核不通过'
      this.reviewForm.remark = ''
      this.reviewOpen = true
    },
    submitReview() {
      this.$refs['reviewForm'].validate(valid => {
        if (valid) {
          reviewPersonReject(this.currentRow.subsidyType, this.currentRow.id, this.reviewForm.remark).then(() => {
            this.$modal.msgSuccess('复核不通过')
            this.reviewOpen = false
            this.detailOpen = false
            this.getList()
          })
        }
      })
    },
    getSubsidyTypeLabel(subsidyType) {
      const typeMap = {
        land_loss_resident: '失地居民',
        expropriatee: '被征地农民',
        demolition_resident: '拆迁居民',
        village_official: '村干部',
        teacher: '教龄补助',
        '1': '失地居民',
        '2': '被征地居民',
        '3': '拆迁居民',
        '4': '村干部',
        '5': '教龄补助'
      }
      return typeMap[subsidyType] || subsidyType || '-'
    }
  }
}
</script>
