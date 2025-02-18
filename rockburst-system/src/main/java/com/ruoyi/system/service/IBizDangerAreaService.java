package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizDangerAreaService extends MPJDeepService<BizDangerArea>
{


    public BizDangerArea selectEntityById(Long id);


    public MPage<BizDangerArea> selectEntityList(BizDangerAreaDto dto, Pagination pagination);


    public int insertEntity(BizDangerAreaDto dto);


    public int updateEntity(BizDangerAreaDto dto);



    public int initPresetPoint(Long workfaceId);





}
