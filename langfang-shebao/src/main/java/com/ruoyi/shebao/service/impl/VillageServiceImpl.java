package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.AdministrativeDivision;
import com.ruoyi.shebao.domain.Village;
import com.ruoyi.shebao.dto.VillageListReq;
import com.ruoyi.shebao.dto.VillageListResp;
import com.ruoyi.shebao.mapper.VillageMapper;
import com.ruoyi.shebao.service.AdministrativeDivisionService;
import com.ruoyi.shebao.service.VillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 村级单位Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-27
 */
@Service
public class VillageServiceImpl extends ServiceImpl<VillageMapper, Village> implements VillageService
{
    @Autowired
    private AdministrativeDivisionService administrativeDivisionService;

    /**
     * 查询村级单位列表
     *
     * @param req 查询条件
     * @return 村级单位列表
     */
    @Override
    public Page<VillageListResp> selectVillageList(VillageListReq req)
    {
        Page<VillageListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return this.baseMapper.selectVillageList(page, req);
    }

    /**
     * 查询村级单位详情
     *
     * @param id 村级单位主键
     * @return 村级单位
     */
    @Override
    public Village selectVillageById(Long id)
    {
        return this.baseMapper.selectById(id);
    }

    /**
     * 新增村级单位
     *
     * @param village 村级单位
     * @return 结果
     */
    @Override
    public int insertVillage(Village village)
    {
        // 构建全路径信息
        buildFullPath(village);

        village.setCreateTime(LocalDateTime.now());
        village.setCreateBy(SecurityUtils.getUsername());
        return this.baseMapper.insert(village);
    }

    /**
     * 修改村级单位
     *
     * @param village 村级单位
     * @return 结果
     */
    @Override
    public int updateVillage(Village village)
    {
        // 重新构建全路径信息
        buildFullPath(village);

        village.setUpdateTime(LocalDateTime.now());
        village.setUpdateBy(SecurityUtils.getUsername());
        return this.baseMapper.updateById(village);
    }

    /**
     * 批量删除村级单位
     *
     * @param ids 需要删除的村级单位主键
     * @return 结果
     */
    @Override
    public int deleteVillageByIds(Long[] ids)
    {
        // 检查是否存在关联数据
        for (Long id : ids)
        {
            if (checkVillageRelatedData(id))
            {
                Village village = selectVillageById(id);
                throw new RuntimeException(String.format("村级单位%1$s存在关联数据，不能删除", village.getVillageName()));
            }
        }

        // 逻辑删除
        for (Long id : ids)
        {
            Village village = new Village();
            village.setId(id);
            village.setDelFlag("2");
            village.setUpdateTime(LocalDateTime.now());
            village.setUpdateBy(SecurityUtils.getUsername());
            this.baseMapper.updateById(village);
        }

        return ids.length;
    }

    /**
     * 删除村级单位信息
     *
     * @param id 村级单位主键
     * @return 结果
     */
    @Override
    public int deleteVillageById(Long id)
    {
        return deleteVillageByIds(new Long[] { id });
    }

    /**
     * 根据村编码查询村级单位
     *
     * @param villageCode 村编码
     * @return 村级单位
     */
    @Override
    public Village selectVillageByCode(String villageCode)
    {
        return this.baseMapper.selectVillageByCode(villageCode);
    }

    /**
     * 校验村编码是否唯一
     *
     * @param village 村级单位信息
     * @return 结果
     */
    @Override
    public String checkVillageCodeUnique(Village village)
    {
        Long villageId = StringUtils.isNull(village.getId()) ? -1L : village.getId();
        Village info = this.baseMapper.selectVillageByCode(village.getVillageCode());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != villageId.longValue())
        {
            return "1"; // 不唯一
        }
        return "0"; // 唯一
    }

    /**
     * 检查村级单位是否存在关联数据
     *
     * @param villageId 村级单位ID
     * @return 结果
     */
    @Override
    public boolean checkVillageRelatedData(Long villageId)
    {
        int count = this.baseMapper.checkVillageRelatedData(villageId);
        return count > 0;
    }

    /**
     * 批量导入村级单位
     *
     * @param villageList 村级单位列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importVillage(List<Village> villageList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(villageList) || villageList.size() == 0)
        {
            throw new RuntimeException("导入村级单位数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (Village village : villageList)
        {
            try
            {
                // 验证是否存在这个村级单位
                Village v = this.baseMapper.selectVillageByCode(village.getVillageCode());
                if (StringUtils.isNull(v))
                {
                    village.setCreateBy(operName);
                    village.setCreateTime(LocalDateTime.now());
                    this.insertVillage(village);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、村级单位 " + village.getVillageName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    village.setId(v.getId());
                    village.setUpdateBy(operName);
                    village.setUpdateTime(LocalDateTime.now());
                    this.updateVillage(village);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、村级单位 " + village.getVillageName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、村级单位 " + village.getVillageName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、村级单位 " + village.getVillageName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        return successMsg.toString();
    }

    /**
     * 构建村级单位的全路径信息
     *
     * @param village 村级单位
     */
    private void buildFullPath(Village village)
    {
        if (StringUtils.isEmpty(village.getParentCode()))
        {
            // 没有父级编码，全路径就是自己
            village.setFullCode(village.getVillageCode());
            village.setFullName(village.getVillageName());
            return;
        }

        // 获取父级行政区划的全路径信息

        AdministrativeDivision parentDivision = administrativeDivisionService.selectByDivisionCode(village.getParentCode());
        if (parentDivision != null)
        {
            // 构建完整的全路径
            String fullCode = StringUtils.isEmpty(parentDivision.getFullCode()) ?
                parentDivision.getDivisionCode() : parentDivision.getFullCode();
            String fullName = StringUtils.isEmpty(parentDivision.getFullName()) ?
                parentDivision.getDivisionName() : parentDivision.getFullName();

            village.setFullCode(fullCode + "/" + village.getVillageCode());
            village.setFullName(fullName + "/" + village.getVillageName());
        }
        else
        {
            // 父级不存在，全路径就是自己
            village.setFullCode(village.getVillageCode());
            village.setFullName(village.getVillageName());
        }
    }
}
