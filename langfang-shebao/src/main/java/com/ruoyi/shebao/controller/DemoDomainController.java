package com.ruoyi.shebao.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.PageResp;
import com.ruoyi.shebao.domain.DemoDomain;
import com.ruoyi.shebao.dto.DemoListReq;
import com.ruoyi.shebao.dto.DemoListResp;
import com.ruoyi.shebao.service.DemoDomainService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shebao/demo")
@RequiredArgsConstructor
public class DemoDomainController extends BaseController {

    private final DemoDomainService demoDomainService;

    @GetMapping("/list")
    R<PageResp<DemoListResp>> list(DemoListReq req) {
        Page<DemoDomain> domainPage = demoDomainService.page(getPage(req), demoDomainService.lambdaQuery()
                .like(StringUtils.isNotBlank(req.getName()), DemoDomain::getName, req.getName())
                .getWrapper());
        PageResp<DemoListResp> respPageResp = PageResp.of(domainPage.getCurrent(), domainPage.getSize(), domainPage.getTotal());
        respPageResp.setRecords(domainPage.getRecords().stream()
                .map(item -> BeanUtil.copyProperties(item, DemoListResp.class))
                .toList());
        return R.ok(respPageResp);
    }


}
