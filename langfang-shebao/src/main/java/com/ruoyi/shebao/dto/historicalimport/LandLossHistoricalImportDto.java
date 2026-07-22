package com.ruoyi.shebao.dto.historicalimport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.Type;
import lombok.Data;

/**
 * 失地居民历史数据导入模板
 */
@Data
public class LandLossHistoricalImportDto
{
    // ===== 个人基本信息 =====
    @Excel(name = "姓名", sort = 1)
    private String name;

    @Excel(name = "身份证号", sort = 2)
    private String idCardNo;

    @Excel(name = "户籍所在地", sort = 3)
    private String householdRegistration;

    @Excel(name = "家庭住址", sort = 4)
    private String homeAddress;

    @Excel(name = "联系电话", sort = 5)
    private String phone;

    @Excel(name = "所属街道办", sort = 6)
    private String streetOfficeName;

    @Excel(name = "所属村委会", sort = 7)
    private String villageCommitteeName;

    @Excel(name = "是否健在", sort = 8, combo = {"是", "否"})
    private String isAlive;

    @Excel(name = "参保状态", sort = 9)
    private String subsidyStatus;

    @Excel(name = "人员状态", sort = 10)
    private String personStatus;

    @Excel(name = "注销时间", sort = 11, dateFormat = "yyyy-MM-dd")
    private String cancelTime;

    @Excel(name = "注销原因", sort = 12)
    private String cancelReason;

    // ===== 失地登记信息 =====
    @Excel(name = "征地时间", sort = 13, dateFormat = "yyyy-MM-dd")
    private String landRequisitionTime;

    @Excel(name = "完成补偿时间", sort = 14, dateFormat = "yyyy-MM-dd")
    private String compensationCompleteTime;

    @Excel(name = "认定时间", sort = 15, dateFormat = "yyyy-MM-dd")
    private String recognitionTime;

    @Excel(name = "征地批次", sort = 16)
    private String landRequisitionBatch;

    @Excel(name = "认定时所在村街", sort = 17)
    private String villageStreet;

    @Excel(name = "是否村合作经济组织成员", sort = 18, combo = {"是", "否"})
    private String isVillageCoopMember;

    @Excel(name = "登记备注", sort = 19)
    private String remark;

    // ===== 待遇核定（选填一组，有则整组必填） =====
    @Excel(name = "发放机构", sort = 20)
    private String grantOrg;

    @Excel(name = "开户名", sort = 21)
    private String accountName;

    @Excel(name = "与参保人关系", sort = 22)
    private String relationToInsured;

    @Excel(name = "银行账号", sort = 23)
    private String bankAccount;

    @Excel(name = "到龄年月", sort = 24, dateFormat = "yyyy-MM")
    private String eligibleYearMonth;

    @Excel(name = "补贴标准", sort = 25)
    private String subsidyStandard;

    @Excel(name = "享受开始年月", sort = 26, dateFormat = "yyyy-MM")
    private String benefitStartMonth;

    @Excel(name = "补发月数", sort = 27)
    private Integer benefitMonths;

    @Excel(name = "补发金额", sort = 28)
    private String benefitAmount;

    // ===== 待遇暂停（选填一组，有则整组必填） =====
    @Excel(name = "暂停年月", sort = 29, dateFormat = "yyyy-MM")
    private String pauseMonth;

    @Excel(name = "暂停原因", sort = 30)
    private String pauseReason;

    @Excel(name = "追回开始年月", sort = 31, dateFormat = "yyyy-MM")
    private String recoverStartMonth;

    @Excel(name = "追回结束年月", sort = 32, dateFormat = "yyyy-MM")
    private String recoverEndMonth;

    @Excel(name = "需要追回金额", sort = 33)
    private String recoverAmount;

    /** 仅失败导出使用 */
    @Excel(name = "失败原因", sort = 99, type = Type.EXPORT)
    private String failureReason;
}
