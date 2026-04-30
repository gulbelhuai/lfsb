package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.domain.PaymentPlanSummary;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.PaymentPlanMapper;
import com.ruoyi.shebao.mapper.PaymentPlanSummaryMapper;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentPlanServiceImpl implements PaymentPlanService
{
    private static final String TYPE_NORMAL = "normal";

    @Autowired
    private PaymentPlanMapper paymentPlanMapper;
    @Autowired
    private PaymentPlanSummaryMapper paymentPlanSummaryMapper;
    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Override
    public Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req)
    {
        Page<PaymentPlanListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return paymentPlanMapper.selectPaymentPlanList(page, req);
    }

    @Override
    public PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req)
    {
        validateReq(req.getDeterminationType(), req.getBusinessPeriod());
        PaymentPlanPreviewResp resp = buildBasePreview(req.getDeterminationType(), req.getBusinessPeriod());
        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            return resp;
        }
        LocalDate businessPeriod = parseBusinessPeriod(req.getBusinessPeriod());
        List<PaymentPlanDetailResp> details = paymentPlanDetailMapper.selectPreviewDetails(businessPeriod);
        resp.setDetailList(details);
        fillSummaryAndTotal(resp, details);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generate(PaymentPlanGenerateReq req)
    {
        PaymentPlanPreviewReq previewReq = new PaymentPlanPreviewReq();
        BeanUtils.copyProperties(req, previewReq);
        PaymentPlanPreviewResp preview = preview(previewReq);
        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            throw new ServiceException("二次发放暂未实现");
        }
        if (preview.getDetailList().isEmpty())
        {
            throw new ServiceException("没有可生成的支付计划数据");
        }
        LocalDate period = parseBusinessPeriod(req.getBusinessPeriod());
        Date now = new Date();
        String operatorName = resolveOperatorName();

        PaymentPlan plan = new PaymentPlan();
        plan.setDeterminationType(req.getDeterminationType());
        plan.setBusinessPeriod(period);
        plan.setTotalCount(preview.getTotalCount());
        plan.setTotalAmount(preview.getTotalAmount());
        plan.setOperatorName(operatorName);
        plan.setOperatorTime(now);
        plan.setApprovalStatus("pending_review");
        plan.setDelFlag("0");
        plan.setCreateBy(SecurityUtils.getUsername());
        plan.setCreateTime(now);
        plan.setUpdateBy(SecurityUtils.getUsername());
        plan.setUpdateTime(now);
        paymentPlanMapper.insert(plan);

        List<PaymentPlanSummary> summaryRows = preview.getSummaryList().stream().map(item ->
        {
            PaymentPlanSummary row = new PaymentPlanSummary();
            row.setPlanId(plan.getId());
            row.setBusinessPeriod(period);
            row.setSubsidyType(item.getSubsidyType());
            row.setGrantOrg(item.getGrantOrg());
            row.setTotalCount(item.getTotalCount());
            row.setTotalAmount(item.getTotalAmount());
            row.setDelFlag("0");
            row.setCreateBy(SecurityUtils.getUsername());
            row.setCreateTime(now);
            row.setUpdateBy(SecurityUtils.getUsername());
            row.setUpdateTime(now);
            return row;
        }).toList();
        if (!summaryRows.isEmpty())
        {
            paymentPlanSummaryMapper.batchInsert(summaryRows);
        }

        List<PaymentPlanDetail> detailRows = preview.getDetailList().stream().map(item ->
        {
            PaymentPlanDetail row = new PaymentPlanDetail();
            row.setPlanId(plan.getId());
            row.setDeterminationId(item.getDeterminationId());
            row.setDeterminationItemId(item.getDeterminationItemId());
            row.setSubsidyType(item.getSubsidyType());
            row.setStreetName(item.getStreetName());
            row.setVillageName(item.getVillageName());
            row.setPersonName(item.getPersonName());
            row.setIdCardNo(item.getIdCardNo());
            row.setBusinessPeriod(period);
            row.setPaymentMonth(item.getPaymentMonth());
            row.setDistributionAmount(item.getDistributionAmount());
            row.setGrantOrg(item.getGrantOrg());
            row.setAccountName(item.getAccountName());
            row.setBankAccount(item.getBankAccount());
            row.setRelationToInsured(item.getRelationToInsured());
            row.setDelFlag("0");
            row.setCreateBy(SecurityUtils.getUsername());
            row.setCreateTime(now);
            row.setUpdateBy(SecurityUtils.getUsername());
            row.setUpdateTime(now);
            return row;
        }).toList();
        if (!detailRows.isEmpty())
        {
            paymentPlanDetailMapper.batchInsert(detailRows);
        }
        return plan.getId();
    }

    @Override
    public List<PaymentPlanSummaryResp> selectSummaryByPlanId(Long planId)
    {
        return paymentPlanSummaryMapper.selectByPlanId(planId);
    }

    @Override
    public Page<PaymentPlanDetailResp> selectDetailByPlanId(Long planId, Integer pageNum, Integer pageSize)
    {
        Page<PaymentPlanDetailResp> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        return paymentPlanDetailMapper.selectByPlanId(page, planId);
    }

    private void validateReq(String determinationType, String businessPeriod)
    {
        if (determinationType == null || determinationType.isBlank())
        {
            throw new ServiceException("请选择核定方式");
        }
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            throw new ServiceException("请选择业务期");
        }
    }

    private PaymentPlanPreviewResp buildBasePreview(String determinationType, String businessPeriod)
    {
        PaymentPlanPreviewResp resp = new PaymentPlanPreviewResp();
        resp.setDeterminationType(determinationType);
        resp.setBusinessPeriod(businessPeriod);
        resp.setOperatorName(resolveOperatorName());
        resp.setOperatorTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return resp;
    }

    private LocalDate parseBusinessPeriod(String businessPeriod)
    {
        return LocalDate.parse(businessPeriod + "-01");
    }

    private void fillSummaryAndTotal(PaymentPlanPreviewResp resp, List<PaymentPlanDetailResp> details)
    {
        Map<String, List<PaymentPlanDetailResp>> grouped = details.stream()
                .collect(Collectors.groupingBy(item -> defaultValue(item.getSubsidyType()) + "||" + defaultValue(item.getGrantOrg())));
        List<PaymentPlanSummaryResp> summaryList = new ArrayList<>();
        for (Map.Entry<String, List<PaymentPlanDetailResp>> entry : grouped.entrySet())
        {
            List<PaymentPlanDetailResp> groupRows = entry.getValue();
            PaymentPlanSummaryResp summary = new PaymentPlanSummaryResp();
            summary.setBusinessPeriod(resp.getBusinessPeriod());
            summary.setSubsidyType(groupRows.get(0).getSubsidyType());
            summary.setGrantOrg(groupRows.get(0).getGrantOrg());
            summary.setTotalCount(groupRows.size());
            summary.setTotalAmount(groupRows.stream()
                    .map(PaymentPlanDetailResp::getDistributionAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            summaryList.add(summary);
        }
        summaryList.sort(Comparator
                .comparing((PaymentPlanSummaryResp o) -> defaultValue(o.getSubsidyType()))
                .thenComparing(o -> defaultValue(o.getGrantOrg())));
        resp.setSummaryList(summaryList);
        resp.setTotalCount(details.size());
        resp.setTotalAmount(summaryList.stream()
                .map(PaymentPlanSummaryResp::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private String defaultValue(String value)
    {
        return value == null ? "" : value;
    }

    private String resolveOperatorName()
    {
        try
        {
            var loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null)
            {
                String nickName = loginUser.getUser().getNickName();
                if (nickName != null && !nickName.isBlank())
                {
                    return nickName;
                }
            }
        }
        catch (Exception ignored)
        {
            // 回退到用户名
        }
        return SecurityUtils.getUsername();
    }
}
