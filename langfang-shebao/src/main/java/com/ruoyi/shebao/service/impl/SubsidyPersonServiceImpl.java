package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.SubsidyPersonCancelReq;
import com.ruoyi.shebao.dto.SubsidyPersonListReq;
import com.ruoyi.shebao.dto.SubsidyPersonListResp;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.service.CommonService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.StreetOfficeService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 被补贴人基础信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubsidyPersonServiceImpl extends ServiceImpl<SubsidyPersonMapper, SubsidyPerson> implements SubsidyPersonService
{
    private final StreetOfficeService streetOfficeService;
    private final VillageCommitteeService villageCommitteeService;
    private final CommonService commonService;

    /**
     * 查询被补贴人基础信息列表
     * 
     * 说明：支持通过isAlive参数过滤注销人员
     *      - isAlive='1': 只查健在人员
     *      - isAlive='0': 只查注销人员
     *      - 不传isAlive: 查询所有人员
     *
     * @param req 查询条件
     * @return 被补贴人基础信息列表
     */
    @Override
    public Page<SubsidyPersonListResp> selectSubsidyPersonList(SubsidyPersonListReq req)
    {
        Page<SubsidyPerson> page = new Page<>(req.getPageNum(), req.getPageSize());
        Page<SubsidyPerson> entityPage = this.page(page, this.lambdaQuery()
                .like(StringUtils.isNotBlank(req.getName()), SubsidyPerson::getName, req.getName())
                .eq(StringUtils.isNotBlank(req.getIdCardNo()), SubsidyPerson::getIdCardNo, req.getIdCardNo())
                .eq(StringUtils.isNotBlank(req.getGender()), SubsidyPerson::getGender, req.getGender())
                .like(StringUtils.isNotBlank(req.getPhone()), SubsidyPerson::getPhone, req.getPhone())
                .like(StringUtils.isNotBlank(req.getHouseholdRegistration()), SubsidyPerson::getHouseholdRegistration, req.getHouseholdRegistration())
                .eq(StringUtils.isNotBlank(req.getIsAlive()), SubsidyPerson::getIsAlive, req.getIsAlive())
                .eq(StringUtils.isNotBlank(req.getIsVillageCoopMember()), SubsidyPerson::getIsVillageCoopMember, req.getIsVillageCoopMember())
                .eq(req.getStreetOfficeId() != null, SubsidyPerson::getStreetOfficeId, req.getStreetOfficeId())
                .eq(req.getVillageCommitteeId() != null, SubsidyPerson::getVillageCommitteeId, req.getVillageCommitteeId())
                .like(StringUtils.isNotBlank(req.getUserCode()), SubsidyPerson::getUserCode, req.getUserCode())
                .eq(StringUtils.isNotBlank(req.getStatus()), SubsidyPerson::getStatus, req.getStatus())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), SubsidyPerson::getApprovalStatus, req.getApprovalStatus())
                .orderByDesc(SubsidyPerson::getCreateTime)
                .getWrapper());

        Page<SubsidyPersonListResp> respPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())) {
            // 预查询街道办和村委会信息
            Map<Long, StreetOffice> streetOfficeMap = streetOfficeService.lambdaQuery()
                    .in(StreetOffice::getId, entityPage.getRecords().stream().map(SubsidyPerson::getStreetOfficeId).filter(Objects::nonNull).toList())
                    .list()
                    .stream()
                    .collect(Collectors.toMap(StreetOffice::getId, streetOffice -> streetOffice));

            Map<Long, VillageCommittee> villageCommitteeMap = villageCommitteeService.lambdaQuery()
                    .in(VillageCommittee::getId, entityPage.getRecords().stream().map(SubsidyPerson::getVillageCommitteeId).filter(Objects::nonNull).toList())
                    .list()
                    .stream()
                    .collect(Collectors.toMap(VillageCommittee::getId, villageCommittee -> villageCommittee));

            respPage.setRecords(entityPage.getRecords().stream()
                    .map(entity -> {
                        SubsidyPersonListResp resp = BeanUtil.copyProperties(entity, SubsidyPersonListResp.class);
                        resp.setStreetOfficeName(Optional.ofNullable(streetOfficeMap.get(entity.getStreetOfficeId())).map(StreetOffice::getStreetName).orElse(null));
                        resp.setVillageCommitteeName(Optional.ofNullable(villageCommitteeMap.get(entity.getVillageCommitteeId())).map(VillageCommittee::getVillageName).orElse(null));
                        resp.setApprovalStatus(entity.getApprovalStatus());
                        return resp;
                    }).toList());
        }

        return respPage;
    }

    /**
     * 查询被补贴人基础信息详情
     *
     * @param id 被补贴人基础信息主键
     * @return 被补贴人基础信息
     */
    @Override
    public SubsidyPerson selectSubsidyPersonById(Long id)
    {
        return this.getById(id);
    }

    /**
     * 新增被补贴人基础信息
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    @Override
    public int insertSubsidyPerson(SubsidyPerson subsidyPerson)
    {
        // 数据校验
        validateSubsidyPerson(subsidyPerson);

        // 根据身份证号自动计算生日和性别
        if (StringUtils.isNotEmpty(subsidyPerson.getIdCardNo()))
        {
            if (subsidyPerson.getBirthday() == null)
            {
                subsidyPerson.setBirthday(parseBirthdayFromIdCard(subsidyPerson.getIdCardNo()));
            }
            if (StringUtils.isEmpty(subsidyPerson.getGender()))
            {
                subsidyPerson.setGender(parseGenderFromIdCard(subsidyPerson.getIdCardNo()));
            }
        }

        // 生成用户编号
        if (StringUtils.isEmpty(subsidyPerson.getUserCode()) &&
            subsidyPerson.getStreetOfficeId() != null &&
            subsidyPerson.getVillageCommitteeId() != null)
        {
            subsidyPerson.setUserCode(generateUserCode(subsidyPerson.getStreetOfficeId(), subsidyPerson.getVillageCommitteeId()));
        }

        // 设置默认值
        if (StringUtils.isEmpty(subsidyPerson.getIsAlive()))
        {
            subsidyPerson.setIsAlive("1"); // 默认健在
        }
        if (StringUtils.isEmpty(subsidyPerson.getIsVillageCoopMember()))
        {
            subsidyPerson.setIsVillageCoopMember("1"); // 默认是村合作经济组织成员
        }
        if (StringUtils.isEmpty(subsidyPerson.getStatus()))
        {
            subsidyPerson.setStatus("0"); // 默认正常
        }
        if (StringUtils.equals(subsidyPerson.getIsAlive(), "1")) {
            // 健在的人不存在死亡时间
            subsidyPerson.setDeathDate(null);
        }

        subsidyPerson.setCreateTime(LocalDateTime.now());
        subsidyPerson.setCreateBy(SecurityUtils.getUsername());
        return this.save(subsidyPerson) ? 1 : 0;
    }

    /**
     * 修改被补贴人基础信息
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    @Override
    public int updateSubsidyPerson(SubsidyPerson subsidyPerson)
    {
        // 数据校验
        validateSubsidyPerson(subsidyPerson);

        // 根据身份证号自动计算生日和性别
        if (StringUtils.isNotEmpty(subsidyPerson.getIdCardNo()))
        {
            if (subsidyPerson.getBirthday() == null)
            {
                subsidyPerson.setBirthday(parseBirthdayFromIdCard(subsidyPerson.getIdCardNo()));
            }
            if (StringUtils.isEmpty(subsidyPerson.getGender()))
            {
                subsidyPerson.setGender(parseGenderFromIdCard(subsidyPerson.getIdCardNo()));
            }
        }
        if (StringUtils.equals(subsidyPerson.getIsAlive(), "1")) {
            // 健在的人不存在死亡时间
            subsidyPerson.setDeathDate(null);
        }
        subsidyPerson.setUserCode(null);
        subsidyPerson.setUpdateTime(LocalDateTime.now());
        subsidyPerson.setUpdateBy(SecurityUtils.getUsername());
        return this.updateById(subsidyPerson) ? 1 : 0;
    }

    /**
     * 批量删除被补贴人基础信息
     *
     * @param ids 需要删除的被补贴人基础信息主键
     * @return 结果
     */
    @Override
    public int deleteSubsidyPersonByIds(Long[] ids)
    {
        // 检查是否存在关联数据
        for (Long id : ids)
        {
            SubsidyPerson subsidyPerson = this.getById(id);
            if (subsidyPerson == null)
            {
                continue;
            }

            int relatedCount = this.baseMapper.checkSubsidyPersonRelatedData(id);
            if (relatedCount > 0)
            {
                throw new RuntimeException(String.format("被补贴人%1$s存在关联的补贴信息，不能删除", subsidyPerson.getName()));
            }
        }

        // 逻辑删除
        for (Long id : ids)
        {
            SubsidyPerson subsidyPerson = new SubsidyPerson();
            subsidyPerson.setId(id);
            subsidyPerson.setDelFlag("2");
            subsidyPerson.setUpdateTime(LocalDateTime.now());
            subsidyPerson.setUpdateBy(SecurityUtils.getUsername());
            this.updateById(subsidyPerson);
        }

        return ids.length;
    }

    /**
     * 删除被补贴人基础信息信息
     *
     * @param id 被补贴人基础信息主键
     * @return 结果
     */
    @Override
    public int deleteSubsidyPersonById(Long id)
    {
        return deleteSubsidyPersonByIds(new Long[] { id });
    }

    /**
     * 校验身份证号是否唯一
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    @Override
    public String checkIdCardNoUnique(SubsidyPerson subsidyPerson)
    {
        Long subsidyPersonId = Objects.isNull(subsidyPerson.getId()) ? -1L : subsidyPerson.getId();
        SubsidyPerson info = this.lambdaQuery()
                .eq(SubsidyPerson::getIdCardNo, subsidyPerson.getIdCardNo())
                .one();
        if (Objects.nonNull(info) && info.getId().longValue() != subsidyPersonId.longValue())
        {
            return "1"; // 不唯一
        }
        return "0"; // 唯一
    }

    /**
     * 批量导入被补贴人基础信息
     *
     * @param subsidyPersonList 被补贴人基础信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importSubsidyPerson(List<SubsidyPerson> subsidyPersonList, Boolean isUpdateSupport, String operName)
    {
        if (Objects.isNull(subsidyPersonList) || subsidyPersonList.size() == 0)
        {
            throw new RuntimeException("导入被补贴人数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (SubsidyPerson subsidyPerson : subsidyPersonList)
        {
            try
            {
                // 验证是否存在这个被补贴人
                SubsidyPerson s = this.lambdaQuery()
                        .eq(SubsidyPerson::getIdCardNo, subsidyPerson.getIdCardNo())
                        .one();
                if (Objects.isNull(s))
                {
                    subsidyPerson.setCreateBy(operName);
                    subsidyPerson.setCreateTime(LocalDateTime.now());
                    this.insertSubsidyPerson(subsidyPerson);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、被补贴人 " + subsidyPerson.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    subsidyPerson.setId(s.getId());
                    subsidyPerson.setUpdateBy(operName);
                    subsidyPerson.setUpdateTime(LocalDateTime.now());
                    this.updateSubsidyPerson(subsidyPerson);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、被补贴人 " + subsidyPerson.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、被补贴人 " + subsidyPerson.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、被补贴人 " + subsidyPerson.getName() + " 导入失败：";
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
     * 生成用户编号
     *
     * @param streetOfficeId 街道办ID
     * @param villageCommitteeId 村委会ID
     * @return 用户编号
     */
    @Override
    public String generateUserCode(Long streetOfficeId, Long villageCommitteeId)
    {
        return commonService.lockDo("generateUserCode", () -> {
            Assert.isTrue(Objects.nonNull(streetOfficeId) && Objects.nonNull(villageCommitteeId), "街道办和村委会不能为空");

            // 获取街道办编码
            StreetOffice streetOffice = streetOfficeService.getById(streetOfficeId);
            Assert.notNull(streetOffice, "街道办不存在");

            // 获取村委会编码
            VillageCommittee villageCommittee = villageCommitteeService.getById(villageCommitteeId);
            Assert.notNull(villageCommittee, "村委会不存在");
            String prefix = streetOffice.getStreetCode() + villageCommittee.getVillageCode();

            // 查询该村委会下现有居民的最大序号
            SubsidyPerson maxPerson = this.lambdaQuery()
                    .likeRight(SubsidyPerson::getUserCode, prefix)
                    .isNotNull(SubsidyPerson::getUserCode)
                    .orderByDesc(SubsidyPerson::getUserCode)
                    .last("LIMIT 1")
                    .one();
            int nextSequence = 1;
            if (maxPerson != null && StringUtils.isNotEmpty(maxPerson.getUserCode()) && maxPerson.getUserCode().length() >= 10)
            {
                try
                {
                    String sequenceStr = maxPerson.getUserCode().substring(7); // 取最后4位
                    nextSequence = Integer.parseInt(sequenceStr) + 1;
                }
                catch (NumberFormatException e)
                {
                    log.error("生成用户编号错误, maxPersonUserCode={}", maxPerson.getUserCode());
                    throw new IllegalArgumentException("生成用户编号错误");
                }
            }

            // 生成用户编号：街道办编码(3位) + 村委会编码(3位) + 居民顺序号(4位)
            return String.format("%s%04d",
                    prefix,
                    nextSequence);
        });
    }

    /**
     * 校验用户编号是否唯一
     *
     * @param userCode 用户编号
     * @param id 当前记录ID（修改时使用）
     * @return 结果
     */
    @Override
    public String checkUserCodeUnique(String userCode, Long id)
    {
        if (StringUtils.isEmpty(userCode))
        {
            return "0"; // 空值认为唯一
        }

        Long currentId = id == null ? -1L : id;
        SubsidyPerson info = this.lambdaQuery()
                .eq(SubsidyPerson::getUserCode, userCode)
                .one();
        if (Objects.nonNull(info) && info.getId().longValue() != currentId.longValue())
        {
            return "1"; // 不唯一
        }
        return "0"; // 唯一
    }

    /**
     * 根据身份证号查询被补贴人基础信息
     *
     * @param idCardNo 身份证号
     * @return 被补贴人基础信息
     */
    @Override
    public SubsidyPerson selectSubsidyPersonByIdCardNo(String idCardNo)
    {
        return this.lambdaQuery()
                .eq(SubsidyPerson::getIdCardNo, idCardNo)
                .one();
    }

    @Override
    public SubsidyPerson selectAliveSubsidyPersonByIdCardNo(String idCardNo)
    {
        if (StringUtils.isBlank(idCardNo))
        {
            return null;
        }
        return this.lambdaQuery()
                .eq(SubsidyPerson::getIdCardNo, idCardNo)
                .eq(SubsidyPerson::getIsAlive, "1")
                .one();
    }

    @Override
    public SubsidyPerson selectAliveSubsidyPersonById(Long id)
    {
        if (id == null)
        {
            return null;
        }
        return this.lambdaQuery()
                .eq(SubsidyPerson::getId, id)
                .eq(SubsidyPerson::getIsAlive, "1")
                .one();
    }

    @Override
    public int cancelByIdCardNo(SubsidyPersonCancelReq req)
    {
        Assert.notNull(req, "注销登记参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(req.getIdCardNo()), "身份证号不能为空");
        Assert.notNull(req.getDeathDate(), "死亡时间不能为空");

        SubsidyPerson person = this.lambdaQuery()
                .eq(SubsidyPerson::getIdCardNo, req.getIdCardNo())
                .one();
        Assert.notNull(person, "未找到该身份证号对应的人员");

        SubsidyPerson update = new SubsidyPerson();
        update.setId(person.getId());
        update.setIsAlive("0");
        update.setDeathDate(req.getDeathDate());
        update.setRemark(req.getRemark());
        update.setUpdateTime(LocalDateTime.now());
        update.setUpdateBy(SecurityUtils.getUsername());
        return this.updateById(update) ? 1 : 0;
    }
    private void validateSubsidyPerson(SubsidyPerson subsidyPerson)
    {
        // 校验身份证号格式
        if (StringUtils.isNotEmpty(subsidyPerson.getIdCardNo()))
        {
            if (!isValidIdCardNo(subsidyPerson.getIdCardNo()))
            {
                throw new RuntimeException("身份证号格式不正确");
            }
        }

        // 校验手机号格式
        if (StringUtils.isNotEmpty(subsidyPerson.getPhone()))
        {
            if (!isValidPhoneNumber(subsidyPerson.getPhone()))
            {
                throw new RuntimeException("联系电话格式不正确");
            }
        }

        // 校验姓名长度
        if (StringUtils.isNotEmpty(subsidyPerson.getName()) && subsidyPerson.getName().length() > 20)
        {
            throw new RuntimeException("姓名长度不能超过20个字符");
        }
    }

    /**
     * 校验身份证号格式
     *
     * @param idCardNo 身份证号
     * @return 是否有效
     */
    private boolean isValidIdCardNo(String idCardNo)
    {
        if (StringUtils.isEmpty(idCardNo) || idCardNo.length() != 18)
        {
            return false;
        }

        // 基本格式校验：前17位数字，最后一位数字或X
        String pattern = "^[0-9]{17}[0-9Xx]$";
        return Pattern.matches(pattern, idCardNo);
    }

    /**
     * 校验手机号格式
     *
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhoneNumber(String phone)
    {
        if (StringUtils.isEmpty(phone))
        {
            return false;
        }

        // 支持手机号（11位）和固话（7-12位）
        String mobilePattern = "^1[3-9]\\d{9}$";
        String landlinePattern = "^\\d{7,12}$";

        return Pattern.matches(mobilePattern, phone) || Pattern.matches(landlinePattern, phone);
    }

    /**
     * 从身份证号解析生日
     *
     * @param idCardNo 身份证号
     * @return 生日
     */
    private LocalDate parseBirthdayFromIdCard(String idCardNo)
    {
        if (StringUtils.isEmpty(idCardNo) || idCardNo.length() != 18)
        {
            return null;
        }

        try
        {
            String birthdayStr = idCardNo.substring(6, 14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(birthdayStr, formatter);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 从身份证号解析性别
     *
     * @param idCardNo 身份证号
     * @return 性别（1男 2女）
     */
    private String parseGenderFromIdCard(String idCardNo)
    {
        if (StringUtils.isEmpty(idCardNo) || idCardNo.length() != 18)
        {
            return null;
        }

        try
        {
            // 身份证号码倒数第二位（第17位，索引16）为性别码
            // 奇数为男性，偶数为女性
            int genderCode = Integer.parseInt(idCardNo.substring(16, 17));
            return genderCode % 2 == 0 ? "2" : "1"; // 1-男性, 2-女性
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
