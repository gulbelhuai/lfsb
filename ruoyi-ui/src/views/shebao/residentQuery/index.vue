<template>
  <div class="app-container">
    <!-- 居民搜索区域 -->
    <el-card class="box-card" style="margin-bottom: 20px;">
      <div slot="header" class="clearfix">
        <span>居民查询</span>
      </div>
      
      <!-- 搜索输入框 -->
      <div style="margin-bottom: 20px;">
        <el-autocomplete
          v-model="searchKeyword"
          :fetch-suggestions="querySearchAsync"
          placeholder="请输入居民姓名或身份证号，模糊匹配居民信息"
          @select="handleSelectResident"
          clearable
          style="width: 100%"
          size="medium"
        >
          <template slot-scope="{ item }">
            <div class="autocomplete-item">
              <span class="name">{{ item.name }}</span>
              <span class="separator">|</span>
              <span class="id-card">{{ item.idCardNo }}</span>
            </div>
          </template>
        </el-autocomplete>
      </div>

      <!-- 历史查询记录 -->
      <div v-if="searchHistory.length > 0">
        <div style="margin-bottom: 10px; color: #606266; font-size: 14px;">
          <i class="el-icon-time"></i> 最近查询：
        </div>
        <el-tag
          v-for="(item, index) in searchHistory"
          :key="index"
          :type="index === 0 ? 'primary' : ''"
          size="medium"
          closable
          style="margin-right: 8px; margin-bottom: 8px; cursor: pointer;"
          @click="handleHistoryClick(item)"
          @close="handleRemoveHistory(index)"
        >
          <span style="font-weight: bold;">{{ item.name }}</span>
          <span style="margin: 0 4px; color: rgba(255,255,255,0.7);">|</span>
          <span style="font-size: 12px;">{{ item.idCardNo }}</span>
        </el-tag>
      </div>
    </el-card>

    <!-- 居民详细信息 -->
    <div v-if="residentInfo">
      <!-- 基本信息 -->
      <el-card class="box-card" style="margin-bottom: 20px;">
        <div slot="header" class="clearfix">
          <span>基本信息</span>
        </div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="用户编号">{{ residentInfo.userCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ residentInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ residentInfo.gender === '1' ? '男' : residentInfo.gender === '2' ? '女' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ residentInfo.idCardNo }}</el-descriptions-item>
          <el-descriptions-item label="生日">{{ residentInfo.birthday || '-' }}</el-descriptions-item>
          <el-descriptions-item label="户籍所在地" :span="3">{{ residentInfo.householdRegistration || '-' }}</el-descriptions-item>
          <el-descriptions-item label="家庭住址" :span="3">{{ residentInfo.homeAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ residentInfo.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="是否健在">{{ residentInfo.isAlive === '1' ? '是' : residentInfo.isAlive === '0' ? '否' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="死亡时间" v-if="residentInfo.isAlive === '0'">{{ residentInfo.deathDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属街道">{{ residentInfo.streetOfficeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属村委会">{{ residentInfo.villageCommitteeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="是否村合作经济组织成员">{{ residentInfo.isVillageCoopMember === '1' ? '是' : residentInfo.isVillageCoopMember === '0' ? '否' : '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 补贴信息 -->
      <el-card class="box-card" style="margin-bottom: 20px;">
        <div slot="header" class="clearfix">
          <span>补贴信息</span>
        </div>
        
        <!-- 失地居民补贴 -->
        <div v-if="subsidyInfo.landLossResidents && subsidyInfo.landLossResidents.length > 0" style="margin-bottom: 20px;">
          <div class="subsidy-title">失地居民补贴</div>
          <el-table :data="subsidyInfo.landLossResidents" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="认定时间" prop="recognitionTime" width="120" align="center" />
            <el-table-column label="征地时间" prop="landRequisitionTime" width="120" align="center" />
            <el-table-column label="完成补偿时间" prop="compensationCompleteTime" width="120" align="center" />
            <el-table-column label="备注" prop="remark" align="left" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- 被征地居民补贴 -->
        <div v-if="subsidyInfo.expropriateeSubsidies && subsidyInfo.expropriateeSubsidies.length > 0" style="margin-bottom: 20px;">
          <div class="subsidy-title">被征地居民补贴</div>
          <el-table :data="subsidyInfo.expropriateeSubsidies" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="征地批次" prop="landRequisitionBatch" width="120" align="center" />
            <el-table-column label="基准时间" prop="baseDate" width="120" align="center" />
            <el-table-column label="职工养老月数" prop="employeePensionMonths" width="120" align="center" />
            <el-table-column label="灵活就业月数" prop="flexibleEmploymentMonths" width="120" align="center" />
            <el-table-column label="困难补贴年限" prop="difficultySubsidyYears" width="120" align="center" />
            <el-table-column label="基准时年龄" prop="ageAtBaseDate" width="120" align="center" />
            <el-table-column label="补贴年限" prop="subsidyYears" width="100" align="center" />
            <el-table-column label="补贴金额" prop="subsidyAmount" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.subsidyAmount ? scope.row.subsidyAmount.toFixed(2) : '0.00' }}
              </template>
            </el-table-column>
            <el-table-column label="参加城乡保险" prop="joinUrbanRuralInsurance" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.joinUrbanRuralInsurance === '1' ? '是' : scope.row.joinUrbanRuralInsurance === '0' ? '否' : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="参加职工养老" prop="joinEmployeePension" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.joinEmployeePension === '1' ? '是' : scope.row.joinEmployeePension === '0' ? '否' : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" align="left" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- 拆迁居民补贴 -->
        <div v-if="subsidyInfo.demolitionResidents && subsidyInfo.demolitionResidents.length > 0" style="margin-bottom: 20px;">
          <div class="subsidy-title">拆迁居民补贴</div>
          <el-table :data="subsidyInfo.demolitionResidents" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="认定时间" prop="recognitionTime" width="120" align="center" />
            <el-table-column label="拆迁时间" prop="demolitionTime" width="120" align="center" />
            <el-table-column label="拆迁事由" prop="demolitionReason" width="200" align="center" />
            <el-table-column label="备注" prop="remark" align="left" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- 村干部补贴 -->
        <div v-if="subsidyInfo.villageOfficials && subsidyInfo.villageOfficials.length > 0" style="margin-bottom: 20px;">
          <div class="subsidy-title">村干部补贴</div>
          <el-table :data="subsidyInfo.villageOfficials" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="总任职年限" prop="totalServiceYears" width="120" align="center" />
            <el-table-column label="是否有违纪" prop="hasViolation" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.hasViolation === '1' ? '是' : scope.row.hasViolation === '0' ? '否' : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" align="left" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- 无补贴信息提示 -->
        <div v-if="!hasAnySubsidy" style="text-align: center; color: #909399; padding: 20px;">
          <i class="el-icon-info"></i> 该居民暂无补贴信息
        </div>
      </el-card>

      <!-- 补贴发放记录 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>补贴发放记录</span>
        </div>
        <el-table v-loading="loading" :data="distributionList" border size="small">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="补贴类型" prop="subsidyType" width="120" align="center">
            <template slot-scope="scope">
              {{ getSubsidyTypeName(scope.row.subsidyType) }}
            </template>
          </el-table-column>
          <el-table-column label="发放金额" prop="distributionAmount" width="120" align="center">
            <template slot-scope="scope">
              {{ scope.row.distributionAmount ? scope.row.distributionAmount.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column label="发放日期" prop="distributionDate" width="120" align="center" />
          <el-table-column label="发放状态" prop="distributionStatus" width="100" align="center">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.distributionStatus === '1'" type="warning" size="small">待审核</el-tag>
              <el-tag v-else-if="scope.row.distributionStatus === '2'" type="primary" size="small">待发放</el-tag>
              <el-tag v-else-if="scope.row.distributionStatus === '3'" type="danger" size="small">已拒绝</el-tag>
              <el-tag v-else-if="scope.row.distributionStatus === '4'" type="success" size="small">已发放</el-tag>
              <el-tag v-else type="info" size="small">未知</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="createTime" width="160" align="center" />
          <el-table-column label="审核说明" prop="reviewRemark" align="left" show-overflow-tooltip />
        </el-table>
        
        <pagination
          v-show="distributionTotal > 0"
          :total="distributionTotal"
          :page.sync="distributionQueryParams.pageNum"
          :limit.sync="distributionQueryParams.pageSize"
          @pagination="getDistributionList"
        />
      </el-card>
    </div>

    <!-- 无居民信息提示 -->
    <div v-if="!residentInfo" style="text-align: center; color: #909399; padding: 40px;">
      <i class="el-icon-search" style="font-size: 48px; margin-bottom: 16px;"></i>
      <div>请输入居民姓名或身份证号进行查询</div>
    </div>
  </div>
</template>

<script>
import { searchResidents, getResidentDetailInfo, getResidentDistributionList } from "@/api/shebao/residentQuery"

export default {
  name: "ResidentQuery",
  data() {
    return {
      // 遮罩层
      loading: false,
      // 搜索关键词
      searchKeyword: '',
      // 搜索历史记录
      searchHistory: [],
      // 居民信息
      residentInfo: null,
      // 补贴信息
      subsidyInfo: {
        landLossResidents: [],
        expropriateeSubsidies: [],
        demolitionResidents: [],
        villageOfficials: []
      },
      // 发放记录列表
      distributionList: [],
      // 发放记录总数
      distributionTotal: 0,
      // 发放记录查询参数
      distributionQueryParams: {
        pageNum: 1,
        pageSize: 10,
        subsidyPersonId: null
      }
    }
  },
  computed: {
    hasAnySubsidy() {
      return (this.subsidyInfo.landLossResidents && this.subsidyInfo.landLossResidents.length > 0) ||
             (this.subsidyInfo.expropriateeSubsidies && this.subsidyInfo.expropriateeSubsidies.length > 0) ||
             (this.subsidyInfo.demolitionResidents && this.subsidyInfo.demolitionResidents.length > 0) ||
             (this.subsidyInfo.villageOfficials && this.subsidyInfo.villageOfficials.length > 0)
    }
  },
  created() {
    this.loadSearchHistory()
  },
  methods: {
    /** 异步搜索居民 */
    querySearchAsync(queryString, cb) {
      if (queryString.trim().length < 1) {
        cb([])
        return
      }
      
      searchResidents(queryString).then(response => {
        const results = response.data || []
        cb(results.map(item => ({
          value: item.name,
          name: item.name,
          idCardNo: item.idCardNo,
          subsidyPersonId: item.subsidyPersonId
        })))
      }).catch(() => {
        cb([])
      })
    },
    /** 选择居民 */
    handleSelectResident(item) {
      // 清空输入框，提示用户已选择
      this.searchKeyword = item.name + ' | ' + item.idCardNo
      // 直接使用居民ID进行精确查询
      this.queryResident(item.subsidyPersonId)
    },
    /** 点击历史记录 */
    handleHistoryClick(item) {
      // 设置输入框显示
      this.searchKeyword = item.name + ' | ' + item.idCardNo
      // 直接使用居民ID进行精确查询
      this.queryResident(item.subsidyPersonId)
    },
    /** 查询居民信息 */
    queryResident(subsidyPersonId) {
      this.loading = true
      getResidentDetailInfo(null, subsidyPersonId).then(response => {
        this.residentInfo = response.data.residentInfo
        this.subsidyInfo = response.data.subsidyInfo
        
        // 更新搜索历史
        this.updateSearchHistory({
          name: this.residentInfo.name,
          idCardNo: this.residentInfo.idCardNo,
          subsidyPersonId: this.residentInfo.id
        })
        
        // 重置发放记录分页参数
        this.distributionQueryParams.pageNum = 1
        this.distributionQueryParams.subsidyPersonId = this.residentInfo.id
        
        // 查询发放记录
        this.getDistributionList()
      }).catch(() => {
        this.loading = false
      }).finally(() => {
        this.loading = false
      })
    },
    /** 查询发放记录列表 */
    getDistributionList() {
      if (!this.distributionQueryParams.subsidyPersonId) {
        return
      }
      this.loading = true
      getResidentDistributionList(this.distributionQueryParams).then(response => {
        this.distributionList = response.data.records || []
        this.distributionTotal = response.data.total || 0
      }).catch(() => {
        this.distributionList = []
        this.distributionTotal = 0
      }).finally(() => {
        this.loading = false
      })
    },
    /** 获取补贴类型名称 */
    getSubsidyTypeName(subsidyType) {
      const typeMap = {
        '1': '失地居民补贴',
        '2': '被征地居民补贴',
        '3': '拆迁居民补贴',
        '4': '村干部补贴',
        '5': '教龄补助'
      }
      return typeMap[subsidyType] || '未知'
    },
    /** 加载搜索历史 */
    loadSearchHistory() {
      const history = localStorage.getItem('residentSearchHistory')
      if (history) {
        try {
          this.searchHistory = JSON.parse(history)
        } catch (e) {
          this.searchHistory = []
        }
      }
    },
    /** 更新搜索历史 */
    updateSearchHistory(item) {
      // 移除重复项（根据身份证号排重）
      this.searchHistory = this.searchHistory.filter(h => h.idCardNo !== item.idCardNo)
      // 添加到开头
      this.searchHistory.unshift(item)
      // 限制最多10条
      this.searchHistory = this.searchHistory.slice(0, 10)
      // 保存到本地存储
      localStorage.setItem('residentSearchHistory', JSON.stringify(this.searchHistory))
    },
    /** 删除历史记录 */
    handleRemoveHistory(index) {
      // 从数组中移除指定索引的项
      this.searchHistory.splice(index, 1)
      // 保存到本地存储
      localStorage.setItem('residentSearchHistory', JSON.stringify(this.searchHistory))
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}

.subsidy-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409EFF;
}

.autocomplete-item {
  display: flex;
  align-items: center;
  line-height: 30px;
}

.autocomplete-item .name {
  font-weight: bold;
  color: #303133;
  margin-right: 8px;
}

.autocomplete-item .separator {
  color: #DCDFE6;
  margin: 0 8px;
}

.autocomplete-item .id-card {
  font-size: 13px;
  color: #909399;
}
</style>
