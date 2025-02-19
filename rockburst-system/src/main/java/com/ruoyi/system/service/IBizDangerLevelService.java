package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerLevel;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizDangerLevelService extends MPJDeepService<BizDangerLevel>
{


    public BizDangerLevel selectEntityById(Long id);


    public MPage<BizDangerLevel> selectEntityList(BizDangerLevel dto, Pagination pagination);


    public int insertEntity(BizDangerLevel dto);


    public int updateEntity(BizDangerLevel dto);







}
