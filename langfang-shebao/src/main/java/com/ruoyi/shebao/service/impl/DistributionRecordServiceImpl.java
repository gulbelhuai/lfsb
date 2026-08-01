package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.DistributionRecordExportResp;
import com.ruoyi.shebao.dto.DistributionRecordListReq;
import com.ruoyi.shebao.dto.ResidentPaymentDetailResp;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.service.DistributionRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistributionRecordServiceImpl implements DistributionRecordService
{
    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Override
    public Page<ResidentPaymentDetailResp> selectList(DistributionRecordListReq req)
    {
        Page<ResidentPaymentDetailResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return paymentPlanDetailMapper.selectDistributionRecordList(page, req);
    }

    @Override
    public List<DistributionRecordExportResp> selectExportList(DistributionRecordListReq req)
    {
        Page<ResidentPaymentDetailResp> page = new Page<>(1, Integer.MAX_VALUE);
        Page<ResidentPaymentDetailResp> result = paymentPlanDetailMapper.selectDistributionRecordList(page, req);
        return result.getRecords().stream().map(row -> {
            DistributionRecordExportResp exp = new DistributionRecordExportResp();
            BeanUtils.copyProperties(row, exp);
            return exp;
        }).collect(Collectors.toList());
    }
}
