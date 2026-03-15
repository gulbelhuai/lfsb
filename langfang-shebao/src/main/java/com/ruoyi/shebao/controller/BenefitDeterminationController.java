package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.dto.BenefitDeterminationImportDto;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 待遇核定Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/benefit/determination")
public class BenefitDeterminationController extends BaseController
{
    @Autowired
    private IBenefitDeterminationService benefitDeterminationService;

    /**
     * 查询待遇核定列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:list')")
    @GetMapping("/list")
    public TableDataInfo list(BenefitDeterminationListReq req)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<BenefitDeterminationListResp> page = benefitDeterminationService.selectBenefitDeterminationList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 获取待遇核定详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(benefitDeterminationService.selectBenefitDeterminationDetail(id));
    }

    /**
     * 新增待遇核定
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:add')")
    @Log(title = "待遇核定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BenefitDetermination benefitDetermination)
    {
        return toAjax(benefitDeterminationService.insertBenefitDetermination(benefitDetermination));
    }

    /**
     * 修改待遇核定
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:edit')")
    @Log(title = "待遇核定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BenefitDetermination benefitDetermination)
    {
        return toAjax(benefitDeterminationService.updateBenefitDetermination(benefitDetermination));
    }

    /**
     * 删除待遇核定
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:remove')")
    @Log(title = "待遇核定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(benefitDeterminationService.deleteBenefitDeterminationByIds(ids));
    }

    /**
     * 提交审核
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:submit')")
    @Log(title = "待遇核定", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable Long id)
    {
        return toAjax(benefitDeterminationService.submitForReview(id));
    }

    /**
     * 导入模板
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:import')")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<BenefitDeterminationImportDto> util = new ExcelUtil<>(BenefitDeterminationImportDto.class);
        util.importTemplateExcel(response, "待遇核定导入模板");
    }

    /**
     * 批量导入待遇核定
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:import')")
    @Log(title = "待遇核定导入", businessType = BusinessType.IMPORT)
    @PostMapping("/batch")
    public AjaxResult batchImport(@RequestParam(required = false) String noticeBatchNo,
                                  @RequestPart("file") MultipartFile file,
                                  @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) throws Exception
    {
        ExcelUtil<BenefitDeterminationImportDto> util = new ExcelUtil<>(BenefitDeterminationImportDto.class);
        List<BenefitDeterminationImportDto> rows = util.importExcel(file.getInputStream());
        int count = benefitDeterminationService.batchImport(noticeBatchNo, rows, attachments);
        return AjaxResult.success("导入成功，共处理 " + count + " 条记录");
    }

    /**
     * 上传单条待遇核定附件ZIP
     */
    @PreAuthorize("@ss.hasAnyPermi('shebao:benefit:determination:add,shebao:benefit:determination:edit,shebao:benefit:determination:import')")
    @PostMapping("/attachment/upload")
    public AjaxResult uploadAttachment(@RequestParam Long determinationId, @RequestPart("file") MultipartFile file) throws Exception
    {
        return AjaxResult.success(benefitDeterminationService.uploadAttachment(determinationId, file));
    }

    /**
     * 导出/打印待遇核定表（单条）
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:determination:print')")
    @Log(title = "待遇核定表", businessType = BusinessType.EXPORT)
    @PostMapping("/print/{id}")
    public void print(@PathVariable Long id, HttpServletResponse response)
    {
        BenefitDeterminationPrintDto dto = benefitDeterminationService.buildPrintDto(id);
        ExcelUtil<BenefitDeterminationPrintDto> util = new ExcelUtil<>(BenefitDeterminationPrintDto.class);
        util.exportExcel(response, java.util.List.of(dto), "待遇核定表");
    }
}
