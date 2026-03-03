package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.LandLossResidentService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一人员登记Controller
 * 提供统一的接口路径，内部路由到具体的业务Controller
 *
 * @author ruoyi
 * @date 2026-01-19
 */
@RestController
@RequestMapping("/shebao/person/registration")
public class PersonRegistrationController extends BaseController
{
    @Autowired
    private LandLossResidentService landLossResidentService;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private ApprovalLogService approvalLogService;

    /**
     * 查询人员登记列表（统一接口，根据subsidyType路由）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) String subsidyType,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String idCardNo,
                          @RequestParam(required = false) String status,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // 默认查询失地居民，如果没有指定subsidyType
        if (subsidyType == null || subsidyType.isEmpty() || "land_loss_resident".equals(subsidyType)) {
            LandLossResidentListReq req = new LandLossResidentListReq();
            req.setName(name);
            req.setIdCardNo(idCardNo);
            req.setStatus(status);
            req.setPageNum(pageNum);
            req.setPageSize(pageSize);

            Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
            return AjaxResult.success(page);
        }

        // 其他类型暂时返回空列表
        Map<String, Object> result = new HashMap<>();
        result.put("rows", new java.util.ArrayList<>());
        result.put("total", 0L);
        return AjaxResult.success(result);
    }

    /**
     * 获取人员登记详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(landLossResidentService.selectLandLossResidentFormById(id));
    }

    /**
     * 新增人员登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:add')")
    @Log(title = "人员登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LandLossResidentFormDto formDto)
    {
        return toAjax(landLossResidentService.insertLandLossResident(formDto));
    }

    /**
     * 修改人员登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:edit')")
    @Log(title = "人员登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LandLossResidentFormDto formDto)
    {
        return toAjax(landLossResidentService.updateLandLossResident(formDto));
    }

    /**
     * 删除人员登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:remove')")
    @Log(title = "人员登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(landLossResidentService.deleteLandLossResidentByIds(ids));
    }

    /**
     * 导出人员登记列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:export')")
    @Log(title = "人员登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                      @RequestParam(required = false) String name,
                      @RequestParam(required = false) String idCardNo)
    {
        LandLossResidentListReq req = new LandLossResidentListReq();
        req.setName(name);
        req.setIdCardNo(idCardNo);
        Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
        com.ruoyi.common.utils.poi.ExcelUtil<LandLossResidentListResp> util =
            new com.ruoyi.common.utils.poi.ExcelUtil<LandLossResidentListResp>(LandLossResidentListResp.class);
        util.exportExcel(response, page.getRecords(), "人员登记数据");
    }

    /**
     * 提交审核
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:submit')")
    @Log(title = "人员登记", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable("id") Long id, @RequestBody(required = false) String remark)
    {
        LandLossResident resident = landLossResidentService.getById(id);
        if (resident == null)
        {
            return AjaxResult.error("记录不存在");
        }
        com.ruoyi.shebao.domain.SubsidyPerson person = subsidyPersonService.getById(resident.getSubsidyPersonId());
        if (person == null)
        {
            return AjaxResult.error("被补贴人信息不存在");
        }
        String currentStatus = person.getApprovalStatus();
        if (!"draft".equals(currentStatus) && !"rejected".equals(currentStatus) && !"approved".equals(currentStatus))
        {
            return AjaxResult.error("当前状态不允许提交审核");
        }
        person.setApprovalStatus("pending_review");
        subsidyPersonService.updateById(person);
        approvalLogService.log("person_register", id, "pending_review", "submit", remark);
        return AjaxResult.success("提交审核成功");
    }
}
