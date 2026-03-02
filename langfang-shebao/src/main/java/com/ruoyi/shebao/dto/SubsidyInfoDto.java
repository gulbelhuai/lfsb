package com.ruoyi.shebao.dto;

import lombok.Data;

import java.util.List;

/**
 * 补贴信息DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class SubsidyInfoDto
{
    /** 失地居民补贴列表 */
    private List<LandLossResidentDto> landLossResidents;

    /** 被征地居民补贴列表 */
    private List<ExpropriateeSubsidyDto> expropriateeSubsidies;

    /** 拆迁居民补贴列表 */
    private List<DemolitionResidentDto> demolitionResidents;

    /** 村干部补贴列表 */
    private List<VillageOfficialDto> villageOfficials;
}
