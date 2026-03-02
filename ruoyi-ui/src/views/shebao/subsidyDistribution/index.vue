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
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="请选择补贴类型" clearable>
          <el-option label="失地居民补贴" value="1" />
          <el-option label="被征地居民补贴" value="2" />
          <el-option label="拆迁居民补贴" value="3" />
          <el-option label="村干部补贴" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放状态" prop="distributionStatus">
        <el-select v-model="queryParams.distributionStatus" placeholder="请选择发放状态" clearable>
          <el-option label="待审核" value="1" />
          <el-option label="待发放" value="2" />
          <el-option label="已拒绝" value="3" />
          <el-option label="已发放" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放日期">
        <el-date-picker
          v-model="distributionDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleDateRangeChange"
        />
      </el-form-item>
      <el-form-item label="归属村镇" prop="villageCode">
        <DivisionSelector
          v-model="queryParams.villageCode"
          mode="select"
          scenario="village"
          placeholder="请选择归属村镇"
          filterable
          clearable
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
          v-hasPermi="['shebao:subsidyDistribution:add']"
        >新增发放</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['shebao:subsidyDistribution:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:subsidyDistribution:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="distributionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="id" width="80" />
      <el-table-column label="姓名" align="center" prop="name" width="100" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="180" />
      <el-table-column label="所属街道办" align="center" prop="streetOfficeName" width="140" />
      <el-table-column label="所属村委会" align="center" prop="villageCommitteeName" width="140" />
      <el-table-column label="补贴类型" align="center" prop="subsidyType" width="140">
        <template slot-scope="scope">
          {{ getSubsidyTypeName(scope.row.subsidyType) }}
        </template>
      </el-table-column>
      <el-table-column label="发放金额" align="center" prop="distributionAmount" width="120">
        <template slot-scope="scope">
          {{ scope.row.distributionAmount ? scope.row.distributionAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发放日期" align="center" prop="distributionDate" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.distributionDate || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发放状态" align="center" prop="distributionStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.distributionStatus === '1'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.distributionStatus === '2'" type="primary">待发放</el-tag>
          <el-tag v-else-if="scope.row.distributionStatus === '3'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="scope.row.distributionStatus === '4'" type="success">已发放</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['shebao:subsidyDistribution:query']"
          >详情</el-button>
          <el-button
            v-if="scope.row.distributionStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-edit-outline"
            @click="handleReview(scope.row)"
            v-hasPermi="['shebao:subsidyDistribution:approve']"
          >审批</el-button>
          <el-button
            v-if="scope.row.distributionStatus === '2'"
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handleReview(scope.row)"
            v-hasPermi="['shebao:subsidyDistribution:distribute']"
          >发放</el-button>
          <el-button
            v-if="scope.row.distributionStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['shebao:subsidyDistribution:remove']"
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

    <!-- 新增发放记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">

        <!-- 步骤1：选择居民和补贴 -->
        <div v-if="step === 1" class="form-section">
          <div class="section-title">
            <i class="el-icon-user"></i>
            选择居民
          </div>

          <el-row>
            <el-col :span="24">
              <el-form-item label="身份证或姓名" prop="keyword" :inline="false">
                <el-autocomplete
                  v-model="form.keyword"
                  :fetch-suggestions="querySearchAsync"
                  placeholder="请输入身份证号或姓名，模糊匹配居民信息"
                  @select="handleSelectResident"
                  clearable
                  style="width: 100%"
                >
                  <template slot-scope="{ item }">
                    <div>{{ item.name }} {{ item.idCardNo }}</div>
                  </template>
                </el-autocomplete>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 区域1：居民信息 -->
          <div v-if="residentInfo" class="resident-info">
            <div class="section-title">
              <i class="el-icon-info"></i>
              居民信息
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
              <el-descriptions-item label="是否健在" :span="residentInfo.isAlive === '0' ? 1 : 2">{{ residentInfo.isAlive === '1' ? '是' : residentInfo.isAlive === '0' ? '否' : '-' }}</el-descriptions-item>
              <el-descriptions-item label="死亡时间" v-if="residentInfo.isAlive === '0'">{{ residentInfo.deathDate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="所属街道">{{ residentInfo.streetOfficeName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="所属村委会">{{ residentInfo.villageCommitteeName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="是否村合作经济组织成员">{{ residentInfo.isVillageCoopMember === '1' ? '是' : residentInfo.isVillageCoopMember === '0' ? '否' : '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 区域2：可发放补贴列表 -->
          <div v-if="residentInfo" class="subsidy-selection">
            <div class="section-title">
              <i class="el-icon-money"></i>
              可发放补贴
            </div>

            <div v-if="!residentInfo.subsidyTypes || residentInfo.subsidyTypes.length === 0">
              <el-empty description="该居民没有可发放的补贴"></el-empty>
            </div>

            <div v-for="typeInfo in residentInfo.subsidyTypes" :key="typeInfo.subsidyType" class="subsidy-type-section">
              <div class="subsidy-type-title">{{ typeInfo.subsidyTypeName }}</div>
              <el-radio-group v-model="selectedSubsidyRecord">
                <div v-for="record in typeInfo.records" :key="record.subsidyRecordId" class="subsidy-record-item">
                  <el-radio :label="typeInfo.subsidyType + '_' + record.subsidyRecordId">
                    <template v-if="typeInfo.subsidyType === '1'">
                      <span>认定时间：{{ record.subsidyDetail.recognitionTime || '-' }}</span>
                      <span style="margin-left: 15px">征地时间：{{ record.subsidyDetail.landRequisitionTime || '-' }}</span>
                      <span style="margin-left: 15px">完成补偿时间：{{ record.subsidyDetail.compensationCompleteTime || '-' }}</span>
                      <span style="margin-left: 15px" v-if="record.subsidyDetail.remark">备注：{{ record.subsidyDetail.remark }}</span>
                    </template>
                    <template v-else-if="typeInfo.subsidyType === '3'">
                      <span>认定时间：{{ record.subsidyDetail.recognitionTime || '-' }}</span>
                      <span style="margin-left: 15px">拆迁时间：{{ record.subsidyDetail.demolitionTime || '-' }}</span>
                      <span style="margin-left: 15px">拆迁事由：{{ record.subsidyDetail.demolitionReason || '-' }}</span>
                      <span style="margin-left: 15px" v-if="record.subsidyDetail.remark">备注：{{ record.subsidyDetail.remark }}</span>
                    </template>
                    <template v-else-if="typeInfo.subsidyType === '2' || typeInfo.subsidyType === '4'">
                      <span>认定时间：{{ record.subsidyDetail.recognitionTime || '-' }}</span>
                      <span>待完善...</span>
                    </template>
                  </el-radio>
                </div>
              </el-radio-group>
            </div>
          </div>
        </div>

        <!-- 步骤2：补贴详情和发放金额 -->
        <div v-if="step === 2" class="form-section">
          <!-- 居民基本信息 -->
          <div class="section-title">
            <i class="el-icon-user"></i>
            居民信息
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
            <el-descriptions-item label="是否健在" :span="residentInfo.isAlive === '0' ? 1 : 2">{{ residentInfo.isAlive === '1' ? '是' : residentInfo.isAlive === '0' ? '否' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="死亡时间" v-if="residentInfo.isAlive === '0'">{{ residentInfo.deathDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="所属街道">{{ residentInfo.streetOfficeName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="所属村委会">{{ residentInfo.villageCommitteeName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="是否村合作经济组织成员">{{ residentInfo.isVillageCoopMember === '1' ? '是' : residentInfo.isVillageCoopMember === '0' ? '否' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="补贴类型" :span="3">{{ getSubsidyTypeName(form.subsidyType) }}</el-descriptions-item>
          </el-descriptions>

          <!-- 补贴完整信息 -->
          <div class="section-title" style="margin-top: 15px">
            <i class="el-icon-document"></i>
            补贴详情
          </div>
          <el-descriptions :column="3" border size="small" v-if="selectedSubsidyDetail">
            <!-- 失地居民补贴 -->
            <template v-if="form.subsidyType === '1'">
              <el-descriptions-item label="认定时间">{{ selectedSubsidyDetail.recognitionTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="征地时间">{{ selectedSubsidyDetail.landRequisitionTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="完成补偿时间">{{ selectedSubsidyDetail.compensationCompleteTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="备注" :span="3" v-if="selectedSubsidyDetail.remark">{{ selectedSubsidyDetail.remark }}</el-descriptions-item>
            </template>
            <!-- 被征地居民补贴 -->
            <template v-else-if="form.subsidyType === '2'">
              <el-descriptions-item label="认定时间" :span="2">{{ selectedSubsidyDetail.recognitionTime || '-' }}</el-descriptions-item>
            </template>
            <!-- 拆迁居民补贴 -->
            <template v-else-if="form.subsidyType === '3'">
              <el-descriptions-item label="认定时间">{{ selectedSubsidyDetail.recognitionTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="拆迁时间">{{ selectedSubsidyDetail.demolitionTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="拆迁事由">{{ selectedSubsidyDetail.demolitionReason || '-' }}</el-descriptions-item>
              <el-descriptions-item label="备注" :span="3" v-if="selectedSubsidyDetail.remark">{{ selectedSubsidyDetail.remark }}</el-descriptions-item>
            </template>
            <!-- 村干部补贴 -->
            <template v-else-if="form.subsidyType === '4'">
              <el-descriptions-item label="认定时间" :span="2">{{ selectedSubsidyDetail.recognitionTime || '-' }}</el-descriptions-item>
            </template>
          </el-descriptions>

          <!-- 发放金额和备注 -->
           <el-row style="margin-top: 15px">
             <el-col :span="6">
               <el-form-item label="发放金额" prop="distributionAmount" label-width="80px">
                 <el-input-number
                   v-model="form.distributionAmount"
                   :min="0.01"
                   :precision="2"
                   :controls="false"
                   placeholder="请输入发放金额"
                   style="width: calc(100% - 40px)"
                 />
                 <span style="margin-left: 10px; color: #909399;">元</span>
               </el-form-item>
             </el-col>
             <el-col :span="18">
               <el-form-item label="备注" prop="remark" label-width="80px">
                 <el-input
                   v-model="form.remark"
                   placeholder="请输入备注信息（选填）"
                   clearable
                   style="width: 100%"
                 />
               </el-form-item>
             </el-col>
           </el-row>

          <!-- 最近发放记录 -->
          <div class="section-title" style="margin-top: 15px">
            <i class="el-icon-tickets"></i>
            最近发放记录（最多显示5条）
            <el-button type="text" size="small" icon="el-icon-refresh" @click="loadRecentDistributions" style="margin-left: 10px">刷新</el-button>
          </div>
          <el-table v-if="recentDistributions.length > 0" :data="recentDistributions" border size="small" style="margin-top: 10px" max-height="200">
            <el-table-column label="姓名" prop="name" width="100" align="center" />
            <el-table-column label="身份证" prop="idCardNo" width="180" align="center" />
            <el-table-column label="补贴类型" prop="subsidyType" width="140" align="center">
              <template slot-scope="scope">
                {{ getSubsidyTypeName(scope.row.subsidyType) }}
              </template>
            </el-table-column>
            <el-table-column label="发放金额" prop="distributionAmount" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.distributionAmount ? scope.row.distributionAmount.toFixed(2) : '0.00' }}
              </template>
            </el-table-column>
            <el-table-column label="提交日期" prop="createTime" width="180" align="center" />
            <el-table-column label="发放状态" prop="distributionStatus" width="100" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.distributionStatus === '1'" type="warning" size="small">待审核</el-tag>
                <el-tag v-else-if="scope.row.distributionStatus === '2'" type="primary" size="small">待发放</el-tag>
                <el-tag v-else-if="scope.row.distributionStatus === '3'" type="danger" size="small">已拒绝</el-tag>
                <el-tag v-else-if="scope.row.distributionStatus === '4'" type="success" size="small">已发放</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发放日期" prop="distributionDate" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.distributionDate || '-' }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无发放记录" :image-size="80" style="margin-top: 10px"></el-empty>
        </div>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="step > 1" @click="prevStep">上一步</el-button>
        <el-button v-if="step < 2" type="primary" @click="nextStep" :disabled="!selectedSubsidyRecord">下一步</el-button>
        <el-button v-if="step === 2" type="primary" @click="submitForm">确认提交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情/审核对话框 -->
    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="1000px" append-to-body>
      <!-- 居民完整信息 -->
      <div class="section-title">
        <i class="el-icon-user"></i>
        居民信息
      </div>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="用户编号">{{ detailInfo.userCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailInfo.gender === '1' ? '男' : detailInfo.gender === '2' ? '女' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ detailInfo.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ detailInfo.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="户籍所在地" :span="3">{{ detailInfo.householdRegistration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="3">{{ detailInfo.homeAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailInfo.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="是否健在" :span="detailInfo.isAlive === '0' ? 1 : 2">{{ detailInfo.isAlive === '1' ? '是' : detailInfo.isAlive === '0' ? '否' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="死亡时间" v-if="detailInfo.isAlive === '0'">{{ detailInfo.deathDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属街道">{{ detailInfo.streetOfficeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属村委会">{{ detailInfo.villageCommitteeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="是否村合作经济组织成员">{{ detailInfo.isVillageCoopMember === '1' ? '是' : detailInfo.isVillageCoopMember === '0' ? '否' : '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 补贴及发放信息 -->
      <div class="section-title" style="margin-top: 15px">
        <i class="el-icon-document"></i>
        补贴及发放信息
      </div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="补贴类型">{{ getSubsidyTypeName(detailInfo.subsidyType) }}</el-descriptions-item>
        <el-descriptions-item label="发放状态">
          <el-tag v-if="detailInfo.distributionStatus === '1'" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailInfo.distributionStatus === '2'" type="primary">待发放</el-tag>
          <el-tag v-else-if="detailInfo.distributionStatus === '3'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="detailInfo.distributionStatus === '4'" type="success">已发放</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发放金额">{{ detailInfo.distributionAmount ? detailInfo.distributionAmount.toFixed(2) : '0.00' }} 元</el-descriptions-item>
        <el-descriptions-item label="发放日期">{{ detailInfo.distributionDate || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审核操作 -->
      <div v-if="showReviewAction" style="margin-top: 15px">
        <el-form :model="reviewForm" :rules="reviewRules" ref="reviewForm" label-width="100px">
          <el-form-item label="审核说明" prop="remark">
            <el-input
              v-model="reviewForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入说明（驳回时原因必填）"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 审核记录 -->
      <div class="review-records" style="margin-top: 15px">
        <div class="section-title">
          <i class="el-icon-time"></i>
          审核记录
        </div>
        <el-table :data="reviewRecords" border size="small" style="margin-top: 10px">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="操作类型" prop="operationType" width="100" align="center">
            <template slot-scope="scope">
              {{ getOperationTypeName(scope.row.operationType) }}
            </template>
          </el-table-column>
          <el-table-column label="操作人" prop="operatorName" width="120" align="center" />
          <el-table-column label="操作时间" prop="operationTime" width="160" align="center" />
          <el-table-column label="备注" prop="operationRemark" align="left" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.operationRemark || '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          v-if="detailInfo.distributionStatus === '1' && showReviewAction"
          type="success"
          @click="handleApproveSubmit"
          v-hasPermi="['shebao:subsidyDistribution:approve']"
        >同意</el-button>
        <el-button
          v-if="detailInfo.distributionStatus === '2' && showReviewAction"
          type="primary"
          @click="handleDistributeSubmit"
          v-hasPermi="['shebao:subsidyDistribution:distribute']"
        >发放</el-button>
        <el-button
          v-if="detailInfo.distributionStatus === '1' && showReviewAction"
          type="danger"
          @click="handleRejectSubmit"
          v-hasPermi="['shebao:subsidyDistribution:approve']"
        >拒绝</el-button>
        <el-button
          v-if="detailInfo.distributionStatus === '2' && showReviewAction"
          type="danger"
          @click="handleRejectSubmit"
          v-hasPermi="['shebao:subsidyDistribution:distribute']"
        >拒绝</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSubsidyDistribution,
  getSubsidyDistribution,
  searchResidents,
  getAvailableSubsidies,
  getRecentDistributions,
  addSubsidyDistribution,
  approveDistribution,
  rejectDistribution,
  distributeSubsidy,
  resubmitDistribution,
  delSubsidyDistribution
} from "@/api/shebao/subsidyDistribution"
import DivisionSelector from "@/components/DivisionSelector"

export default {
  name: "SubsidyDistribution",
  components: {
    DivisionSelector
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 发放记录表格数据
      distributionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 发放日期范围
      distributionDateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        subsidyType: null,
        distributionStatus: null,
        distributionDateStart: null,
        distributionDateEnd: null,
        villageCode: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        keyword: [
          { required: true, message: "请输入身份证号或姓名", trigger: "blur" }
        ],
        distributionAmount: [
          { required: true, message: "请输入发放金额", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value === null || value === undefined || value === '') {
                callback(new Error('请输入发放金额'))
              } else if (value <= 0) {
                callback(new Error('发放金额必须大于0'))
              } else {
                callback()
              }
            },
            trigger: "blur"
          }
        ]
      },
      // 当前步骤
      step: 1,
      // 居民信息
      residentInfo: null,
      // 选中的补贴记录
      selectedSubsidyRecord: null,
      // 选中的补贴详情
      selectedSubsidyDetail: null,
      // 最近发放记录
      recentDistributions: [],
      // 详情对话框
      detailOpen: false,
      detailTitle: "",
      detailInfo: {},
      reviewRecords: [],
      showReviewAction: false,
      reviewType: '',
      reviewForm: {
        remark: ''
      },
      reviewRules: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询发放记录列表 */
    getList() {
      this.loading = true
      listSubsidyDistribution(this.queryParams).then(response => {
        this.distributionList = response.data.records
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
      this.step = 1
      this.residentInfo = null
      this.selectedSubsidyRecord = null
      this.selectedSubsidyDetail = null
      this.recentDistributions = []
      this.form = {
        keyword: null,
        subsidyPersonId: null,
        subsidyType: null,
        subsidyRecordId: null,
        distributionAmount: null,
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
      this.distributionDateRange = []
      this.queryParams.distributionDateStart = null
      this.queryParams.distributionDateEnd = null
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增补贴发放"
    },
    /** 查看详情 */
    handleView(row) {
      this.showReviewAction = false
      this.showDetail(row.id)
    },
    /** 审批操作 */
    handleReview(row) {
      this.showReviewAction = true
      this.reviewType = 'review'
      this.showDetail(row.id)
    },
    /** 显示详情 */
    showDetail(id) {
      getSubsidyDistribution(id).then(response => {
        this.detailInfo = response.data.distribution
        this.reviewRecords = response.data.reviewRecords
        this.detailOpen = true
        this.detailTitle = this.showReviewAction ? "审核发放记录" : "发放记录详情"
      })
    },
    /** 审核通过提交 */
    handleApproveSubmit() {
      approveDistribution(this.detailInfo.id, this.reviewForm.remark).then(response => {
        this.$modal.msgSuccess("审核通过")
        this.detailOpen = false
        this.getList()
      })
    },
    /** 拒绝提交 */
    handleRejectSubmit() {
      if (!this.reviewForm.remark || this.reviewForm.remark.trim() === '') {
        this.$modal.msgWarning("驳回原因不能为空")
        return
      }
      rejectDistribution(this.detailInfo.id, this.reviewForm.remark).then(response => {
        this.$modal.msgSuccess("已拒绝")
        this.detailOpen = false
        this.getList()
      })
    },
    /** 发放提交 */
    handleDistributeSubmit() {
      distributeSubsidy(this.detailInfo.id, this.reviewForm.remark).then(response => {
        this.$modal.msgSuccess("发放成功")
        this.detailOpen = false
        this.getList()
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除发放记录编号为"' + ids + '"的数据项？').then(() => {
        return delSubsidyDistribution(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('shebao/subsidyDistribution/export', {
        ...this.queryParams
      }, `subsidy_distribution_${new Date().getTime()}.xlsx`)
    },
    /** 自动搜索居民 */
    querySearchAsync(queryString, cb) {
      if (!queryString || queryString.trim().length < 1) {
        cb([])
        return
      }
      searchResidents(queryString.trim()).then(response => {
        cb(response.data)
      }).catch(() => {
        cb([])
      })
    },
    /** 选择居民 */
    handleSelectResident(item) {
      getAvailableSubsidies(item.subsidyPersonId).then(response => {
        this.residentInfo = response.data
        this.selectedSubsidyRecord = null
        this.selectedSubsidyDetail = null
        if (!this.residentInfo.subsidyTypes || this.residentInfo.subsidyTypes.length === 0) {
          this.$message.warning("该居民没有可发放的补贴")
        }
      })
    },
    /** 下一步 */
    nextStep() {
      if (this.step === 1) {
        if (!this.residentInfo) {
          this.$message.warning("请先搜索并选择居民")
          return
        }
        if (!this.selectedSubsidyRecord) {
          this.$message.warning("请选择要发放的补贴")
          return
        }
        // 处理选中的补贴记录
        this.handleSubsidyRecordChange()
        this.step = 2
        // 自动加载最近发放记录
        this.loadRecentDistributions()
      }
    },
    /** 上一步 */
    prevStep() {
      if (this.step === 2) {
        // 返回第一步时清空最近发放记录
        this.recentDistributions = []
      }
      this.step--
    },
    /** 补贴记录选择变化 */
    handleSubsidyRecordChange() {
      const value = this.selectedSubsidyRecord
      if (!value) return

      const [subsidyType, subsidyRecordId] = value.split('_')

      // 查找选中的记录
      for (const typeInfo of this.residentInfo.subsidyTypes) {
        if (typeInfo.subsidyType === subsidyType) {
          for (const record of typeInfo.records) {
            if (record.subsidyRecordId.toString() === subsidyRecordId) {
              this.selectedSubsidyDetail = record.subsidyDetail
              this.form.subsidyPersonId = this.residentInfo.subsidyPersonId
              this.form.subsidyType = subsidyType
              this.form.subsidyRecordId = record.subsidyRecordId
              this.form.distributionAmount = record.subsidyAmount
              break
            }
          }
          break
        }
      }
    },
    /** 加载最近发放记录 */
    loadRecentDistributions() {
      if (!this.residentInfo || !this.residentInfo.subsidyPersonId) {
        return
      }
      getRecentDistributions(this.residentInfo.subsidyPersonId, 5).then(response => {
        this.recentDistributions = response.data || []
      })
    },
    /** 提交表单 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const submitData = {
            subsidyPersonId: this.form.subsidyPersonId,
            subsidyType: this.form.subsidyType,
            subsidyRecordId: this.form.subsidyRecordId,
            distributionAmount: this.form.distributionAmount,
            remark: this.form.remark
          }
          addSubsidyDistribution(submitData).then(response => {
            this.$modal.msgSuccess("提交成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    /** 日期范围变化处理 */
    handleDateRangeChange(timeRange) {
      if (timeRange && timeRange.length === 2) {
        this.queryParams.distributionDateStart = timeRange[0]
        this.queryParams.distributionDateEnd = timeRange[1]
      } else {
        this.queryParams.distributionDateStart = null
        this.queryParams.distributionDateEnd = null
      }
    },
    /** 获取补贴类型名称 */
    getSubsidyTypeName(type) {
      const typeMap = {
        '1': '失地居民补贴',
        '2': '被征地居民补贴',
        '3': '拆迁居民补贴',
        '4': '村干部补贴'
      }
      return typeMap[type] || '未知'
    },
    /** 获取操作类型名称 */
    getOperationTypeName(type) {
      const typeMap = {
        '1': '提交审核',
        '2': '同意',
        '3': '拒绝',
        '4': '发放'
      }
      return typeMap[type] || '未知'
    },
    /** 获取状态样式类 */
    getStatusClass(status) {
      const classMap = {
        '0': 'status-not-distributed',
        '1': 'status-pending-review',
        '2': 'status-pending-distribution',
        '3': 'status-rejected',
        '4': 'status-distributed'
      }
      return classMap[status] || ''
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

.resident-info {
  margin-top: 15px;
}

.subsidy-type-section {
  margin-bottom: 20px;
}

.subsidy-type-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 10px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.subsidy-record-item {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.record-status {
  margin-left: 10px;
  font-size: 12px;
}

.status-not-distributed {
  color: #67c23a;
}

.status-pending-review {
  color: #e6a23c;
}

.status-pending-distribution {
  color: #409eff;
}

.status-rejected {
  color: #f56c6c;
}

.status-distributed {
  color: #909399;
}

.review-records {
  margin-top: 20px;
}
</style>

