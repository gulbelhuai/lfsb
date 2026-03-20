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
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="请选择补贴类型" clearable>
          <el-option label="失地居民" value="land_loss_resident" />
          <el-option label="被征地居民" value="expropriatee" />
          <el-option label="拆迁居民" value="demolition_resident" />
          <el-option label="村干部" value="village_official" />
          <el-option label="教师" value="teacher" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
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
            icon="el-icon-check"
            @click="handlePass(scope.row)"
            v-hasPermi="['shebao:person:review:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-hasPermi="['shebao:person:review:reject']"
          >驳回</el-button>
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
    <el-dialog title="人员登记详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户编号">{{ detailData.userCode }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === '1' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailData.birthday || detailData.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="补贴类型">
          {{ getSubsidyTypeLabel(detailData.subsidyType) }}
        </el-descriptions-item>
        <el-descriptions-item label="街道办事处">{{ detailData.streetOfficeName }}</el-descriptions-item>
        <el-descriptions-item label="村委会">{{ detailData.villageCommitteeName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审批历史 -->
      <el-divider content-position="left">审批历史</el-divider>
      <approval-history :history="approvalHistory" />

      <!-- 复核操作 -->
      <div slot="footer" class="dialog-footer">
        <el-button type="success" @click="handlePass(detailData)" v-hasPermi="['shebao:person:review:approve']">复核通过</el-button>
        <el-button type="danger" @click="handleReject(detailData)" v-hasPermi="['shebao:person:review:reject']">复核驳回</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 复核对话框 -->
    <el-dialog :title="reviewTitle" :visible.sync="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewForm" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="复核意见" prop="remark">
          <el-input v-model="reviewForm.remark" type="textarea" :rows="4" placeholder="请输入复核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReview">确 定</el-button>
        <el-button @click="reviewOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPersonReview, reviewPersonPass, reviewPersonReject } from '@/api/shebao/person'
import { getApprovalHistory } from '@/api/shebao/approval'
import ApprovalHistory from '@/components/Shebao/ApprovalHistory'

export default {
  name: 'PersonReview',
  dicts: ['subsidy_type'],
  components: {
    ApprovalHistory
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 数据列表
      dataList: [],
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 审批历史
      approvalHistory: [],
      // 是否显示复核弹出层
      reviewOpen: false,
      // 复核标题
      reviewTitle: '',
      // 复核类型
      reviewType: '',
      // 当前复核ID
      currentId: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        subsidyType: null,
        approvalStatus: 'pending_review'
      },
      // 复核表单
      reviewForm: {
        remark: null
      },
      // 表单校验
      reviewRules: {
        remark: [
          { required: true, message: '复核意见不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true
      listPersonReview(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
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
    /** 查看详情 */
    handleView(row) {
      this.detailData = row
      this.detailOpen = true
      // 获取审批历史
      getApprovalHistory('person_register', row.id).then(response => {
        this.approvalHistory = response.data
      })
    },
    /** 复核通过 */
    handlePass(row) {
      this.currentId = row.id
      this.reviewType = 'pass'
      this.reviewTitle = '复核通过'
      this.reviewForm.remark = ''
      this.reviewOpen = true
    },
    /** 复核驳回 */
    handleReject(row) {
      this.currentId = row.id
      this.reviewType = 'reject'
      this.reviewTitle = '复核驳回'
      this.reviewForm.remark = ''
      this.reviewOpen = true
    },
    /** 提交复核 */
    submitReview() {
      this.$refs['reviewForm'].validate(valid => {
        if (valid) {
          const reviewFunc = this.reviewType === 'pass' ? reviewPersonPass : reviewPersonReject
          reviewFunc(this.currentId, this.reviewForm.remark).then(response => {
            this.$modal.msgSuccess(this.reviewType === 'pass' ? '复核通过' : '复核驳回')
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
        expropriatee: '被征地居民',
        demolition_resident: '拆迁居民',
        village_official: '村干部',
        teacher: '教师',
        '1': '失地居民',
        '2': '被征地居民',
        '3': '拆迁居民',
        '4': '村干部',
        '5': '教师'
      }
      return typeMap[subsidyType] || subsidyType || '-'
    }
  }
}
</script>

