package com.ruoyi.web.controller.shebao;

import java.util.List;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 社保参数配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/shebao/config")
public class ShebaoConfigController extends BaseController
{
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取社保参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        startPage();
        // 只查询非系统内置且键名以shebao.开头的参数
        config.setConfigType("N"); // N表示非系统内置
        List<SysConfig> list = configService.selectConfigList(config);
        
        // 过滤出键名以shebao.开头的参数
        list = list.stream()
            .filter(item -> item.getConfigKey() != null && item.getConfigKey().startsWith("shebao."))
            .toList();
        
        return getDataTable(list);
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        SysConfig config = configService.selectConfigById(configId);
        // 验证是否为shebao.开头的参数
        if (config != null && config.getConfigKey() != null && config.getConfigKey().startsWith("shebao.")) {
            return success(config);
        }
        return error("参数不存在或无权限访问");
    }

    /**
     * 新增社保参数配置
     */
    @PreAuthorize("@ss.hasPermi('shebao:config:add')")
    @Log(title = "社保参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        // 确保键名以shebao.开头
        if (StringUtils.isEmpty(config.getConfigKey()) || !config.getConfigKey().startsWith("shebao.")) {
            return error("参数键名必须以'shebao.'开头");
        }
        
        // 确保为非系统内置参数
        config.setConfigType("N");
        
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改社保参数配置
     */
    @PreAuthorize("@ss.hasPermi('shebao:config:edit')")
    @Log(title = "社保参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        // 验证是否为shebao.开头的参数
        SysConfig existingConfig = configService.selectConfigById(config.getConfigId());
        if (existingConfig == null || existingConfig.getConfigKey() == null || 
            !existingConfig.getConfigKey().startsWith("shebao.")) {
            return error("参数不存在或无权限修改");
        }
        
        // 如果修改了键名，确保新键名也以shebao.开头
        if (!StringUtils.isEmpty(config.getConfigKey()) && !config.getConfigKey().startsWith("shebao.")) {
            return error("参数键名必须以'shebao.'开头");
        }
        
        // 确保为非系统内置参数
        config.setConfigType("N");
        
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除社保参数配置
     */
    @PreAuthorize("@ss.hasPermi('shebao:config:remove')")
    @Log(title = "社保参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        // 验证所有要删除的参数都是shebao.开头的
        for (Long configId : configIds) {
            SysConfig config = configService.selectConfigById(configId);
            if (config == null || config.getConfigKey() == null || 
                !config.getConfigKey().startsWith("shebao.")) {
                return error("存在无权限删除的参数");
            }
        }
        
        configService.deleteConfigByIds(configIds);
        return success();
    }
}
