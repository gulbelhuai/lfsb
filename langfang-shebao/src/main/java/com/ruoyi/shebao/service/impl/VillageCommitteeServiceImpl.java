package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageCommitteeListReq;
import com.ruoyi.shebao.dto.VillageCommitteeListResp;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.service.StreetOfficeService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 村委会信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-18
 */
@Service
@RequiredArgsConstructor
public class VillageCommitteeServiceImpl extends ServiceImpl<VillageCommitteeMapper, VillageCommittee> implements VillageCommitteeService
{
    private final StreetOfficeService streetOfficeService;

    /**
     * 查询村委会信息列表
     *
     * @param req 查询条件
     * @return 村委会信息列表
     */
    @Override
    public Page<VillageCommitteeListResp> selectVillageCommitteeList(VillageCommitteeListReq req)
    {
        // 设置分页参数默认值
        Integer pageNum = req.pageNumOrDefault();
        Integer pageSize = req.pageSizeOrDefault();
        Page<VillageCommittee> page = new Page<>(pageNum, pageSize);
        Page<VillageCommittee> entityPage = this.page(page, this.lambdaQuery()
                .eq(req.getStreetOfficeId() != null, VillageCommittee::getStreetOfficeId, req.getStreetOfficeId())
                .eq(StringUtils.isNotBlank(req.getVillageCode()), VillageCommittee::getVillageCode, req.getVillageCode())
                .like(StringUtils.isNotBlank(req.getVillageName()), VillageCommittee::getVillageName, req.getVillageName())
                .eq(StringUtils.isNotBlank(req.getStatus()), VillageCommittee::getStatus, req.getStatus())
                .orderByAsc(VillageCommittee::getStreetOfficeId)
                .orderByAsc(VillageCommittee::getVillageCode)
                .getWrapper());
        Page<VillageCommitteeListResp> respPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())) {
            Map<Long, StreetOffice> streetOfficeMap = streetOfficeService.lambdaQuery()
                    .in(StreetOffice::getId, entityPage.getRecords().stream().map(VillageCommittee::getStreetOfficeId).toList())
                    .list()
                    .stream()
                    .collect(Collectors.toMap(StreetOffice::getId, streetOffice -> streetOffice));
            respPage.setRecords(entityPage.getRecords().stream()
                    .map(entity-> {
                        VillageCommitteeListResp resp = BeanUtil.copyProperties(entity, VillageCommitteeListResp.class);
                        resp.setStreetOfficeName(Optional.ofNullable(streetOfficeMap.get(entity.getStreetOfficeId())).map(StreetOffice::getStreetName).orElse(null));
                        return resp;
                    }).toList());
        }

        return respPage;
    }

    /**
     * 查询村委会信息详情
     *
     * @param id 村委会信息主键
     * @return 村委会信息
     */
    @Override
    public VillageCommittee selectVillageCommitteeById(Long id)
    {
        return this.getById(id);
    }

    /**
     * 新增村委会信息
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    @Override
    public int insertVillageCommittee(VillageCommittee villageCommittee)
    {
        return this.save(villageCommittee) ? 1 : 0;
    }

    /**
     * 修改村委会信息
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    @Override
    public int updateVillageCommittee(VillageCommittee villageCommittee)
    {
        return this.updateById(villageCommittee) ? 1 : 0;
    }

    /**
     * 批量删除村委会信息
     *
     * @param ids 需要删除的村委会信息主键集合
     * @return 结果
     */
    @Override
    public int deleteVillageCommitteeByIds(Long[] ids)
    {
        if (ids.length == 0) {
            return 0;
        }
        return this.baseMapper.deleteBatchIds(List.of(ids));
    }

    /**
     * 删除村委会信息信息
     *
     * @param id 村委会信息主键
     * @return 结果
     */
    @Override
    public int deleteVillageCommitteeById(Long id)
    {
        return this.removeById(id) ? 1 : 0;
    }

    /**
     * 校验村委会编码是否唯一
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    @Override
    public String checkVillageCodeUnique(VillageCommittee villageCommittee)
    {
        Long id = Objects.isNull(villageCommittee.getId()) ? -1L : villageCommittee.getId();
        return this.lambdaQuery()
                .eq(VillageCommittee::getStreetOfficeId, villageCommittee.getStreetOfficeId())
                .eq(VillageCommittee::getVillageCode, villageCommittee.getVillageCode())
                .ne(VillageCommittee::getId,  id)
                .exists() ? "1" : "0";
    }

    /**
     * 校验村委会名称是否唯一
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    @Override
    public String checkVillageNameUnique(VillageCommittee villageCommittee)
    {
        Long id = Objects.isNull(villageCommittee.getId()) ? -1L : villageCommittee.getId();
        return this.lambdaQuery()
                .eq(VillageCommittee::getStreetOfficeId, villageCommittee.getStreetOfficeId())
                .eq(VillageCommittee::getVillageName, villageCommittee.getVillageName())
                .ne(VillageCommittee::getId,  id)
                .exists() ? "1" : "0";
    }

    /**
     * 获取所有正常的村委会列表（用于下拉选择）
     *
     * @return 村委会列表
     */
    @Override
    public List<VillageCommittee> selectNormalVillageCommitteeList()
    {
        return this.lambdaQuery()
                .eq(VillageCommittee::getStatus, "0")
                .orderByAsc(VillageCommittee::getVillageCode)
                .list();
    }

    /**
     * 根据街道办ID获取村委会列表（用于下拉选择）
     *
     * @param streetOfficeId 街道办ID
     * @return 村委会列表
     */
    @Override
    public List<VillageCommittee> selectVillageCommitteeListByStreetOfficeId(Long streetOfficeId)
    {
        return this.lambdaQuery()
                .eq(VillageCommittee::getStreetOfficeId, streetOfficeId)
                .eq(VillageCommittee::getStatus, "0")
                .orderByAsc(VillageCommittee::getVillageCode)
                .list();
    }
}
