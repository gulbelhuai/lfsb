package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待遇核定附件对象 shebao_benefit_attachment
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_attachment")
public class BenefitAttachment extends BaseDomain
{
    private String businessType;

    private Long businessId;

    private String noticeBatchNo;

    private Long subsidyPersonId;

    private String originalFileName;

    private String zipFilePath;

    private String extractDir;

    private String previewImagePaths;
}
