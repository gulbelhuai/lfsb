package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.dto.StreetOfficeListReq;
import com.ruoyi.shebao.dto.StreetOfficeListResp;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.service.StreetOfficeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 街道办事处信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-18
 */
@Service
public class StreetOfficeServiceImpl extends ServiceImpl<StreetOfficeMapper, StreetOffice> implements StreetOfficeService
{

    /**
     * 查询街道办事处信息列表
     *
     * @param req 查询条件
     * @return 街道办事处信息列表
     */
    @Override
    public Page<StreetOfficeListResp> selectStreetOfficeList(StreetOfficeListReq req)
    {
        // 设置分页参数默认值
        Integer pageNum = req.pageNumOrDefault();
        Integer pageSize = req.pageSizeOrDefault();
        Page<StreetOffice> page = new Page<>(pageNum, pageSize);
        Page<StreetOffice> entityPage = this.page(page, this.lambdaQuery()
                .eq(StringUtils.isNotBlank(req.getStreetCode()), StreetOffice::getStreetCode, req.getStreetCode())
                .like(StringUtils.isNotBlank(req.getStreetName()), StreetOffice::getStreetName, req.getStreetName())
                .eq(StringUtils.isNotBlank(req.getStatus()), StreetOffice::getStatus, req.getStatus())
                .orderByAsc(StreetOffice::getStreetCode)
                .getWrapper()
        );
        Page<StreetOfficeListResp> respPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())) {
            respPage.setRecords(entityPage.getRecords().stream()
                    .map(entity -> BeanUtil.copyProperties(entity, StreetOfficeListResp.class))
                    .toList()
            );
        }

        return respPage;
    }

    /**
     * 查询街道办事处信息详情
     *
     * @param id 街道办事处信息主键
     * @return 街道办事处信息
     */
    @Override
    public StreetOffice selectStreetOfficeById(Long id)
    {
        return this.getById(id);
    }

    /**
     * 新增街道办事处信息
     *
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    @Override
    public int insertStreetOffice(StreetOffice streetOffice)
    {
        return this.save(streetOffice) ? 1 : 0;
    }

    /**
     * 修改街道办事处信息
     *
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    @Override
    public int updateStreetOffice(StreetOffice streetOffice)
    {
        return this.updateById(streetOffice) ? 1 : 0;
    }

    /**
     * 批量删除街道办事处信息
     *
     * @param ids 需要删除的街道办事处信息主键集合
     * @return 结果
     */
    @Override
    public int deleteStreetOfficeByIds(Long[] ids)
    {
        return this.removeByIds(List.of(ids)) ? ids.length : 0;
    }

    /**
     * 删除街道办事处信息信息
     *
     * @param id 街道办事处信息主键
     * @return 结果
     */
    @Override
    public int deleteStreetOfficeById(Long id)
    {
        return this.removeById(id) ? 1 : 0;
    }

    /**
     * 校验街道办编码是否唯一
     *
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    @Override
    public String checkStreetCodeUnique(StreetOffice streetOffice)
    {
        Long id = Objects.isNull(streetOffice.getId()) ? -1L : streetOffice.getId();
        return this.lambdaQuery()
                .eq(StreetOffice::getStreetCode, streetOffice.getStreetCode())
                .ne(StreetOffice::getId, id)
                .exists() ? "1" : "0";
    }

    /**
     * 校验街道办名称是否唯一
     *
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    @Override
    public String checkStreetNameUnique(StreetOffice streetOffice)
    {
        Long id = Objects.isNull(streetOffice.getId()) ? -1L : streetOffice.getId();
        return this.lambdaQuery()
                .eq(StreetOffice::getStreetName, streetOffice.getStreetName())
                .ne(StreetOffice::getId, id)
                .exists() ? "1" : "0";
    }

    /**
     * 获取所有正常的街道办列表（用于下拉选择）
     *
     * @return 街道办列表
     */
    @Override
    public List<StreetOffice> selectNormalStreetOfficeList()
    {
        LambdaQueryWrapper<StreetOffice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StreetOffice::getStatus, "0")
               .orderByAsc(StreetOffice::getStreetCode);

        return this.list(wrapper);
    }
}
