package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;

/**
 * 待遇核定Service接口
 *
 * @author ruoyi
 * @date 2025-01-19
 */
public interface IBenefitDeterminationService extends IService<BenefitDetermination>
{
    /**
     * 查询待遇核定列表
     *
     * @param req 查询条件
     * @return 待遇核定列表
     */
    Page<BenefitDeterminationListResp> selectBenefitDeterminationList(BenefitDeterminationListReq req);

    /**
     * 查询待遇核定详情
     *
     * @param id 待遇核定ID
     * @return 待遇核定详情
     */
    BenefitDetermination selectBenefitDeterminationById(Long id);

    /**
     * 新增待遇核定
     *
     * @param benefitDetermination 待遇核定
     * @return 结果
     */
    int insertBenefitDetermination(BenefitDetermination benefitDetermination);

    /**
     * 修改待遇核定
     *
     * @param benefitDetermination 待遇核定
     * @return 结果
     */
    int updateBenefitDetermination(BenefitDetermination benefitDetermination);

    /**
     * 批量删除待遇核定
     *
     * @param ids 待遇核定IDs
     * @return 结果
     */
    int deleteBenefitDeterminationByIds(Long[] ids);

    /**
     * 提交审核
     *
     * @param id 待遇核定ID
     * @return 结果
     */
    int submitForReview(Long id);

    /**
     * 复核通过
     *
     * @param id 待遇核定ID
     * @param remark 复核意见
     * @return 结果
     */
    int approveReview(Long id, String remark);

    /**
     * 复核驳回
     *
     * @param id 待遇核定ID
     * @param reason 驳回原因
     * @return 结果
     */
    int rejectReview(Long id, String reason);

    /**
     * 构建待遇核定表（导出/打印）数据
     *
     * @param id 待遇核定ID
     * @return 核定表DTO
     */
    BenefitDeterminationPrintDto buildPrintDto(Long id);
}
