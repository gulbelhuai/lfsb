/**
 * 社保管理系统路由配置
 * 对应后端菜单配置 sql/refactor/04_menu_config.sql
 */
export default {
  path: '/shebao',
  component: Layout,
  hidden: false,
  redirect: 'noRedirect',
  children: [
    // ==================== 1. 人员信息管理 ====================
    {
      path: 'person',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoPersonManage',
      meta: { title: '人员信息管理', icon: 'peoples' }
    },
    // 1.1 失地居民登记
    {
      path: 'person/landloss',
      component: () => import('@/views/shebao/person/landloss/index'),
      name: 'LandLossRegistration',
      meta: { title: '失地居民登记', icon: 'form' },
      hidden: false
    },
    // 1.2 被征地居民登记
    {
      path: 'person/expropriatee',
      component: () => import('@/views/shebao/person/expropriatee/index'),
      name: 'ExpropriateeRegistration',
      meta: { title: '被征地居民登记', icon: 'form' },
      hidden: false
    },
    // 1.3 拆迁居民登记
    {
      path: 'person/demolition',
      component: () => import('@/views/shebao/demolitionResident/index'),
      name: 'DemolitionRegistration',
      meta: { title: '拆迁居民登记', icon: 'form' },
      hidden: false
    },
    // 1.4 村干部登记
    {
      path: 'person/official',
      component: () => import('@/views/shebao/villageOfficial/index'),
      name: 'OfficialRegistration',
      meta: { title: '村干部登记', icon: 'form' },
      hidden: false
    },
    // 1.5 教龄补助登记
    {
      path: 'person/teacher',
      component: () => import('@/views/shebao/person/teacher/index'),
      name: 'TeacherRegistration',
      meta: { title: '教龄补助登记', icon: 'form' },
      hidden: false
    },
    // 1.6 人员登记审核
    {
      path: 'person/review',
      component: () => import('@/views/shebao/person/review/index'),
      name: 'PersonReview',
      meta: { title: '人员登记审核', icon: 'education' },
      hidden: false
    },
    // 1.7 人员信息变更（基本/关键信息变更与审核）
    {
      path: 'person/modify',
      component: () => import('@/views/shebao/person/modify/index'),
      name: 'PersonModify',
      meta: { title: '人员信息变更', icon: 'edit' },
      hidden: false
    },

    // ==================== 2. 待遇核定管理 ====================
    {
      path: 'benefit',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoBenefitManage',
      meta: { title: '待遇核定管理', icon: 'money' }
    },
    // 2.1 预到龄通知生成
    {
      path: 'benefit/notice',
      component: () => import('@/views/shebao/benefit/notice/index'),
      name: 'BenefitNotice',
      meta: { title: '预到龄通知生成', icon: 'message' },
      hidden: false
    },
    // 2.2 待遇核定
    {
      path: 'benefit/determination',
      component: () => import('@/views/shebao/benefit/determination/index'),
      name: 'BenefitDetermination',
      meta: { title: '待遇核定', icon: 'skill' },
      hidden: false
    },
    // 2.3 待遇核定复核
    {
      path: 'benefit/review',
      component: () => import('@/views/shebao/benefit/review/index'),
      name: 'BenefitReview',
      meta: { title: '待遇核定复核', icon: 'education' },
      hidden: false
    },

    // ==================== 3. 待遇管理 ====================
    {
      path: 'management',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoBenefitMgmt',
      meta: { title: '待遇管理', icon: 'list' }
    },
    // 3.1 发放信息修改
    {
      path: 'management/modify',
      component: () => import('@/views/shebao/management/modify/index'),
      name: 'BenefitModify',
      meta: { title: '发放信息修改', icon: 'edit' },
      hidden: false
    },
    // 3.2 待遇暂停/恢复
    {
      path: 'management/suspension',
      component: () => import('@/views/shebao/management/suspension/index'),
      name: 'BenefitSuspension',
      meta: { title: '待遇暂停/恢复', icon: 'lock' },
      hidden: false
    },
    // 3.3 待遇认证
    {
      path: 'management/certification',
      component: () => import('@/views/shebao/management/certification/index'),
      name: 'BenefitCertification',
      meta: { title: '待遇认证', icon: 'validCode' },
      hidden: false
    },

    // ==================== 4. 支付计划管理 ====================
    {
      path: 'payment',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoPaymentManage',
      meta: { title: '支付计划管理', icon: 'tab' }
    },
    // 4.1 支付计划生成
    {
      path: 'payment/plan',
      component: () => import('@/views/shebao/payment/plan/index'),
      name: 'PaymentPlan',
      meta: { title: '支付计划生成', icon: 'build' },
      hidden: false
    },
    // 4.2 支付计划复核
    {
      path: 'payment/review',
      component: () => import('@/views/shebao/payment/review/index'),
      name: 'PaymentReview',
      meta: { title: '支付计划复核', icon: 'education' },
      hidden: false
    },
    // 4.3 支付计划审批
    {
      path: 'payment/approve',
      component: () => import('@/views/shebao/payment/approve/index'),
      name: 'PaymentApprove',
      meta: { title: '支付计划审批', icon: 'pass' },
      hidden: false
    },
    // 4.4 上传财务系统
    {
      path: 'payment/upload',
      component: () => import('@/views/shebao/payment/upload/index'),
      name: 'PaymentUpload',
      meta: { title: '上传财务系统', icon: 'upload' },
      hidden: false
    },

    // ==================== 5. 财务管理 ====================
    {
      path: 'finance',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoFinanceManage',
      meta: { title: '财务管理', icon: 'guide' }
    },
    // 5.1 批次管理
    {
      path: 'finance/batch',
      component: () => import('@/views/shebao/finance/batch/index'),
      name: 'FinanceBatch',
      meta: { title: '批次管理', icon: 'nested' },
      hidden: false
    },
    // 5.2 银行发放
    {
      path: 'finance/bank',
      component: () => import('@/views/shebao/finance/bank/index'),
      name: 'FinanceBank',
      meta: { title: '银行发放', icon: 'component' },
      hidden: false
    },
    // 5.3 失败处理
    {
      path: 'finance/failure',
      component: () => import('@/views/shebao/finance/failure/index'),
      name: 'FinanceFailure',
      meta: { title: '失败处理', icon: 'bug' },
      hidden: false
    },
    // 5.4 财务账户管理
    {
      path: 'finance/account',
      component: () => import('@/views/shebao/finance/account/index'),
      name: 'FinanceAccount',
      meta: { title: '财务账户管理', icon: 'money' },
      hidden: false
    },

    // ==================== 6. 统计管理（原审计） ====================
    {
      path: 'audit',
      component: () => import('@/views/shebao/index'),
      name: 'ShebaoAuditManage',
      meta: { title: '统计管理', icon: 'log' }
    },
    // 6.1 操作日志
    {
      path: 'audit/operlog',
      component: () => import('@/views/shebao/audit/operlog/index'),
      name: 'AuditOperlog',
      meta: { title: '操作日志', icon: 'documentation' },
      hidden: false
    },
    // 6.2 审批历史
    {
      path: 'audit/approval',
      component: () => import('@/views/shebao/audit/approval/index'),
      name: 'AuditApproval',
      meta: { title: '审批历史', icon: 'tree-table' },
      hidden: false
    },
    // 6.3 发放明细
    {
      path: 'audit/detail',
      component: () => import('@/views/shebao/audit/detail/index'),
      name: 'AuditDetail',
      meta: { title: '发放明细', icon: 'list' },
      hidden: false
    },
    // 6.4 统计报表
    {
      path: 'audit/statistics',
      component: () => import('@/views/shebao/audit/statistics/index'),
      name: 'AuditStatistics',
      meta: { title: '统计报表', icon: 'chart' },
      hidden: false
    }
  ]
}
