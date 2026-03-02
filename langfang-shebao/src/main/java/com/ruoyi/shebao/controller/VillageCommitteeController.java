package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageCommitteeListReq;
import com.ruoyi.shebao.dto.VillageCommitteeListResp;
import com.ruoyi.shebao.service.VillageCommitteeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 村委会信息Controller
 *
 * @author ruoyi
 * @date 2025-10-18
 */
@RestController
@RequestMapping("/system/villageCommittee")
public class VillageCommitteeController extends BaseController
{
    @Autowired
    private VillageCommitteeService villageCommitteeService;

    /**
     * 查询村委会信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:list')")
    @GetMapping("/list")
    public AjaxResult list(VillageCommitteeListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<VillageCommitteeListResp> page = villageCommitteeService.selectVillageCommitteeList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出村委会信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:export')")
    @Log(title = "村委会信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VillageCommitteeListReq req)
    {
        req.setPageNum(1);
        req.setPageSize(Integer.MAX_VALUE);
        Page<VillageCommitteeListResp> page = villageCommitteeService.selectVillageCommitteeList(req);
        ExcelUtil<VillageCommitteeListResp> util = new ExcelUtil<VillageCommitteeListResp>(VillageCommitteeListResp.class);
        util.exportExcel(response, page.getRecords(), "村委会信息数据");
    }

    /**
     * 获取村委会信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(villageCommitteeService.selectVillageCommitteeById(id));
    }

    /**
     * 新增村委会信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:add')")
    @Log(title = "村委会信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody VillageCommittee villageCommittee)
    {
        if (!"0".equals(villageCommitteeService.checkVillageCodeUnique(villageCommittee)))
        {
            return AjaxResult.error("新增村委会'" + villageCommittee.getVillageName() + "'失败，村委会编码已存在");
        }
        if (!"0".equals(villageCommitteeService.checkVillageNameUnique(villageCommittee)))
        {
            return AjaxResult.error("新增村委会'" + villageCommittee.getVillageName() + "'失败，村委会名称已存在");
        }
        return toAjax(villageCommitteeService.insertVillageCommittee(villageCommittee));
    }

    /**
     * 修改村委会信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:edit')")
    @Log(title = "村委会信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody VillageCommittee villageCommittee)
    {
        if (!"0".equals(villageCommitteeService.checkVillageCodeUnique(villageCommittee)))
        {
            return AjaxResult.error("修改村委会'" + villageCommittee.getVillageName() + "'失败，村委会编码已存在");
        }
        if (!"0".equals(villageCommitteeService.checkVillageNameUnique(villageCommittee)))
        {
            return AjaxResult.error("修改村委会'" + villageCommittee.getVillageName() + "'失败，村委会名称已存在");
        }
        return toAjax(villageCommitteeService.updateVillageCommittee(villageCommittee));
    }

    /**
     * 删除村委会信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageCommittee:remove')")
    @Log(title = "村委会信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(villageCommitteeService.deleteVillageCommitteeByIds(ids));
    }

    /**
     * 获取所有正常的村委会列表（用于下拉选择）
     */
    @GetMapping("/selectList")
    public AjaxResult selectList()
    {
        List<VillageCommittee> list = villageCommitteeService.selectNormalVillageCommitteeList();
        return AjaxResult.success(list);
    }

    /**
     * 根据街道办ID获取村委会列表（用于下拉选择）
     */
    @GetMapping("/selectList/{streetOfficeId}")
    public AjaxResult selectListByStreetOfficeId(@PathVariable("streetOfficeId") Long streetOfficeId)
    {
        List<VillageCommittee> list = villageCommitteeService.selectVillageCommitteeListByStreetOfficeId(streetOfficeId);
        return AjaxResult.success(list);
    }
}
