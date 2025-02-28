package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;

import java.util.List;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizDangerAreaService extends MPJDeepService<BizDangerArea>
{


    public BizDangerAreaVo selectEntityById(Long id);


    public MPage<BizDangerAreaVo> selectEntityList(BizDangerAreaDto dto, Pagination pagination);


    public List<BizDangerAreaVo> selectEntityCheckList(BizDangerAreaDto dto);


    public List<BizDangerAreaVo> selectEntityListVo(List<Long> areaIdList);


    public int insertEntity(BizDangerAreaDto dto);


    public int updateEntity(BizDangerAreaDto dto);


    public int initPresetPoint(Long workfaceId);







}
