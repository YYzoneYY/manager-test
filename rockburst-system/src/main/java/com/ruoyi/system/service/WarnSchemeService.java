package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.dto.WarnSchemeSelectDTO;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */
public interface WarnSchemeService extends IService<WarnSchemeEntity> {

    int addWarnScheme(WarnSchemeDTO warnSchemeDTO, Long mineId);

    int updateWarnScheme(WarnSchemeDTO warnSchemeDTO);

    WarnSchemeDTO detail(Long warnSchemeId);

    TableData pageQueryList(WarnSchemeSelectDTO warnSchemeSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    int batchEnableDisable(Long[] warnSchemeIds);

    boolean deleteByIds(Long[] warnSchemeIds);
}