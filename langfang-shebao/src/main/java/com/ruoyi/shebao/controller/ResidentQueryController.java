package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.dto.ResidentDetailInfoDto;
import com.ruoyi.shebao.dto.ResidentSearchResp;
import com.ruoyi.shebao.service.ResidentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 居民查询Controller
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@RestController
@RequestMapping("/shebao/residentQuery")
public class ResidentQueryController extends BaseController
{
    @Autowired
    private ResidentQueryService residentQueryService;

    /**
     * 搜索居民
     */
    @GetMapping("/searchResidents")
    public AjaxResult searchResidents(@RequestParam String keyword)
    {
        List<ResidentSearchResp> residents = residentQueryService.searchResidents(keyword);
        return AjaxResult.success(residents);
    }

    /**
     * 获取居民详细信息
     */
    @GetMapping("/getResidentDetailInfo")
    public AjaxResult getResidentDetailInfo(@RequestParam(required = false) String keyword, 
                                          @RequestParam(required = false) Long subsidyPersonId)
    {
        ResidentDetailInfoDto detailInfo = residentQueryService.getResidentDetailInfo(keyword, subsidyPersonId);
        return AjaxResult.success(detailInfo);
    }

    /**
     * 获取居民发放记录
     */
    @GetMapping("/getResidentDistributionList")
    public AjaxResult getResidentDistributionList(@RequestParam Long subsidyPersonId,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize)
    {
        return residentQueryService.getResidentDistributionList(subsidyPersonId, pageNum, pageSize);
    }
}
