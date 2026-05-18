package com.ruoyi.shebao.constant;

/**
 * 补贴登记审批状态（各补贴子表 approval_status 字段）
 */
public final class SubsidyApprovalStatus
{
    public static final String DRAFT = "draft";
    public static final String PENDING_REVIEW = "pending_review";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";

    private SubsidyApprovalStatus()
    {
    }

    public static boolean isApproved(String approvalStatus)
    {
        return APPROVED.equals(approvalStatus);
    }
}
