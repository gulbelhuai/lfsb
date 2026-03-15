const path = require("path");

const DATASETS = {
  "2030-02": {
    noticeMonth: "2030-02",
    fixtureSqlPath: path.resolve(__dirname, "..", "预到龄通知闭环测试数据.sql"),
    fixtureIdCards: [
      "130101197005050011",
      "130101197005180022",
      "130101197005200033",
      "130101197005250044",
      "130101197006010055",
    ],
    primaryTarget: {
      name: "闭环正样本甲",
      idCardNo: "130101197005050011",
      bankAccountName: "闭环正样本甲",
      bankAccount: "6222000000000001",
      benefitStartYear: 2030,
      benefitStartMonth: 5,
    },
  },
  "2030-03": {
    noticeMonth: "2030-03",
    fixtureSqlPath: path.resolve(__dirname, "..", "预到龄通知闭环测试数据_203003.sql"),
    fixtureIdCards: [
      "130101197006050061",
      "130101197006180077",
      "13010119700625008X",
      "130101197006200082",
      "130101197006280094",
      "130101197007010109",
    ],
    primaryTarget: {
      name: "脚本正样本-失地",
      idCardNo: "130101197006050061",
      bankAccountName: "脚本正样本-失地",
      bankAccount: "6222000000000002",
      benefitStartYear: 2030,
      benefitStartMonth: 6,
    },
  },
};

function getBenefitNoticeDataset(noticeMonth) {
  return DATASETS[noticeMonth] || DATASETS["2030-02"];
}

module.exports = {
  DATASETS,
  getBenefitNoticeDataset,
};
