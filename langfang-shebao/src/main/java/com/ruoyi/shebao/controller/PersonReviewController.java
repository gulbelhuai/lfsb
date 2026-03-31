package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.ResidentDetailInfoDto;
import com.ruoyi.shebao.dto.SubsidyPersonListReq;
import com.ruoyi.shebao.dto.SubsidyPersonListResp;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.ResidentQueryService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员登记复核Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/person/review")
public class PersonReviewController extends BaseController
{
    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private ApprovalLogService approvalLogService;

    @Autowired
    private LandLossResidentMapper landLossResidentMapper;

    @Autowired
    private ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    @Autowired
    private DemolitionResidentMapper demolitionResidentMapper;

    @Autowired
    private VillageOfficialMapper villageOfficialMapper;

    @Autowired
    private TeacherSubsidyMapper teacherSubsidyMapper;

    @Autowired
    private ResidentQueryService residentQueryService;

    /**
     * 查询待复核人员列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(SubsidyPersonListReq req, @RequestParam(required = false) String subsidyType)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        if (StringUtils.isBlank(req.getApprovalStatus())) {
            req.setApprovalStatus("pending_review");
        }

        int pageNum = req.getPageNum();
        int pageSize = req.getPageSize();
        req.setPageNum(1);
        req.setPageSize(5000);
        Page<SubsidyPersonListResp> page = subsidyPersonService.selectSubsidyPersonList(req);
        List<Map<String, Object>> filteredRows = new ArrayList<>();
        for (SubsidyPersonListResp item : page.getRecords())
        {
            String resolvedSubsidyType = resolveSubsidyType(item.getId());
            if (StringUtils.isBlank(resolvedSubsidyType))
            {
                continue;
            }
            if (StringUtils.isNotBlank(subsidyType) && !StringUtils.equals(subsidyType, resolvedSubsidyType))
            {
                continue;
            }
            filteredRows.add(buildRow(item, resolvedSubsidyType));
        }

        int fromIndex = Math.min((pageNum - 1) * pageSize, filteredRows.size());
        int toIndex = Math.min(fromIndex + pageSize, filteredRows.size());
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(filteredRows.subList(fromIndex, toIndex));
        rspData.setTotal(filteredRows.size());
        return rspData;
    }

    /**
     * 复核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:approve')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        SubsidyPerson person = subsidyPersonService.getById(id);
        if (person == null)
        {
            return AjaxResult.error("记录不存在");
        }
        if (!"pending_review".equals(person.getApprovalStatus()))
        {
            return AjaxResult.error("当前状态不允许复核");
        }
        person.setApprovalStatus("approved");
        person.setUpdateTime(LocalDateTime.now());
        subsidyPersonService.updateById(person);
        approvalLogService.log("person_register", id, "approved", "approve", remark);
        return AjaxResult.success("复核通过");
    }

    /**
     * 复核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:reject')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam String reason)
    {
        SubsidyPerson person = subsidyPersonService.getById(id);
        if (person == null)
        {
            return AjaxResult.error("记录不存在");
        }
        if (!"pending_review".equals(person.getApprovalStatus()))
        {
            return AjaxResult.error("当前状态不允许驳回");
        }
        person.setApprovalStatus("rejected");
        person.setUpdateTime(LocalDateTime.now());
        subsidyPersonService.updateById(person);
        approvalLogService.log("person_register", id, "rejected", "reject", reason);
        return AjaxResult.success("复核驳回成功");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        SubsidyPerson person = subsidyPersonService.getById(id);
        if (person == null)
        {
            return AjaxResult.error("记录不存在");
        }
        ResidentDetailInfoDto detailInfo = residentQueryService.getResidentDetailInfo(null, id);
        return AjaxResult.success(detailInfo);
    }

    private Map<String, Object> buildRow(SubsidyPersonListResp item, String subsidyType)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getId());
        row.put("subsidyPersonId", item.getId());
        row.put("userCode", item.getUserCode());
        row.put("name", item.getName());
        row.put("idCardNo", item.getIdCardNo());
        row.put("gender", item.getGender());
        row.put("birthday", item.getBirthday());
        row.put("subsidyType", subsidyType);
        row.put("streetOfficeName", item.getStreetOfficeName());
        row.put("villageCommitteeName", item.getVillageCommitteeName());
        row.put("phone", item.getPhone());
        row.put("submitTime", item.getUpdateTime());
        row.put("approvalStatus", item.getApprovalStatus());
        return row;
    }

    private String resolveSubsidyType(Long subsidyPersonId)
    {
        List<LandLossResident> landLossResidents = landLossResidentMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!landLossResidents.isEmpty())
        {
            return "land_loss_resident";
        }
        List<ExpropriateeSubsidy> expropriatees = expropriateeSubsidyMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!expropriatees.isEmpty())
        {
            return "expropriatee";
        }
        if (!demolitionResidentMapper.selectBySubsidyPersonId(subsidyPersonId).isEmpty())
        {
            return "demolition_resident";
        }
        List<VillageOfficial> villageOfficials = villageOfficialMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!villageOfficials.isEmpty())
        {
            return "village_official";
        }
        List<TeacherSubsidy> teacherSubsidies = teacherSubsidyMapper.selectList(new LambdaQueryWrapper<TeacherSubsidy>()
                .eq(TeacherSubsidy::getSubsidyPersonId, subsidyPersonId)
                .eq(TeacherSubsidy::getDelFlag, "0"));
        if (!teacherSubsidies.isEmpty())
        {
            return "teacher";
        }
        return null;
    }
}
