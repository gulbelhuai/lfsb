package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shebao/benefit/notice")
public class BenefitNoticeController extends BaseController
{
    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String noticeMonth,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize)
    {
        int ageThreshold = 60;
        LocalDate targetDate;
        if (noticeMonth != null && !noticeMonth.isEmpty()) {
            YearMonth ym = YearMonth.parse(noticeMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
            targetDate = ym.atDay(1);
        } else {
            targetDate = LocalDate.now().plusMonths(3);
        }

        LocalDate bornAfter = targetDate.minusYears(ageThreshold).minusMonths(3);
        LocalDate bornBefore = targetDate.minusYears(ageThreshold).plusMonths(3);

        List<SubsidyPerson> allPersons = subsidyPersonService.lambdaQuery()
                .eq(SubsidyPerson::getDelFlag, "0")
                .eq(SubsidyPerson::getIsAlive, "1")
                .eq(SubsidyPerson::getApprovalStatus, "approved")
                .le(SubsidyPerson::getBirthday, bornBefore)
                .ge(SubsidyPerson::getBirthday, bornAfter)
                .orderByAsc(SubsidyPerson::getBirthday)
                .list();

        List<Map<String, Object>> resultList = allPersons.stream().map(p -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("subsidyPersonId", p.getId());
            row.put("name", p.getName());
            row.put("idCardNo", p.getIdCardNo());
            int age = Period.between(p.getBirthday(), LocalDate.now()).getYears();
            row.put("currentAge", age);
            LocalDate retireDate = p.getBirthday().plusYears(ageThreshold);
            row.put("retirementDate", retireDate.toString());
            row.put("noticeMonth", targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            row.put("notified", false);
            return row;
        }).collect(Collectors.toList());

        int total = resultList.size();
        int from = Math.min((pageNum - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> pageData = resultList.subList(from, to);

        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(pageData);
        rsp.setTotal(total);
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody(required = false) Map<String, Object> params)
    {
        return AjaxResult.success("预到龄通知已生成，请在待遇核定中为相关人员创建核定记录");
    }
}
