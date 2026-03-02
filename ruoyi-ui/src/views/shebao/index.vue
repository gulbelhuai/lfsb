<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span class="header-title">廊坊社保管理系统</span>
          </div>
          <div class="welcome-content">
            <h2>欢迎使用廊坊社保管理系统</h2>
            <p>本系统提供完整的社保补贴管理功能，包括人员登记、待遇核定、支付计划、财务管理和审计管理。</p>

            <el-divider></el-divider>

            <el-row :gutter="20" class="quick-links">
              <el-col :span="8" v-if="hasPermission('shebao:person:add')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/person/landloss')">
                  <i class="el-icon-user-solid quick-icon"></i>
                  <div class="quick-title">人员登记</div>
                  <div class="quick-desc">快速进行人员信息登记</div>
                </el-card>
              </el-col>

              <el-col :span="8" v-if="hasPermission('shebao:benefit:add')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/benefit/determination')">
                  <i class="el-icon-document-checked quick-icon"></i>
                  <div class="quick-title">待遇核定</div>
                  <div class="quick-desc">进行待遇核定申请</div>
                </el-card>
              </el-col>

              <el-col :span="8" v-if="hasPermission('shebao:payment:add')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/payment/plan')">
                  <i class="el-icon-s-finance quick-icon"></i>
                  <div class="quick-title">支付计划</div>
                  <div class="quick-desc">生成支付计划</div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="quick-links">
              <el-col :span="8" v-if="hasPermission('shebao:finance:list')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/finance/batch')">
                  <i class="el-icon-tickets quick-icon"></i>
                  <div class="quick-title">批次管理</div>
                  <div class="quick-desc">管理发放批次</div>
                </el-card>
              </el-col>

              <el-col :span="8" v-if="hasPermission('shebao:audit:list')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/audit/approval')">
                  <i class="el-icon-view quick-icon"></i>
                  <div class="quick-title">审批历史</div>
                  <div class="quick-desc">查看审批记录</div>
                </el-card>
              </el-col>

              <el-col :span="8" v-if="hasPermission('shebao:audit:list')">
                <el-card shadow="hover" class="quick-card" @click.native="handleQuickNav('/shebao/audit/statistics')">
                  <i class="el-icon-data-line quick-icon"></i>
                  <div class="quick-title">统计报表</div>
                  <div class="quick-desc">查看统计数据</div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'ShebaoIndex',
  data() {
    return {}
  },
  methods: {
    hasPermission(permission) {
      // 使用若依框架的权限判断方法
      return this.checkPermi([permission])
    },
    handleQuickNav(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.welcome-content {
  padding: 20px;
}

.welcome-content h2 {
  color: #409EFF;
  margin-bottom: 15px;
}

.welcome-content p {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.quick-links {
  margin-top: 30px;
}

.quick-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  padding: 20px 0;
}

.quick-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.quick-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 10px;
}

.quick-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.quick-desc {
  font-size: 13px;
  color: #909399;
}

.el-divider {
  margin: 30px 0;
}
</style>
