package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.vo.BizTravePointVo;

import java.util.List;

/**
 * 矿井管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizTravePointService extends IService<BizTravePoint>
{

    MPage<BizTravePointVo> geRuleList(Long locationId,String constructType,Pagination pagination);

    BizTravePoint getPrePointDistance(BizTravePointDto dto);

    List<BizTravePoint>  getQyPoint(Long workfaceId);

    Long getVertexCount(Long pointId, Long tunnelId, Boolean vertex);

    BizTravePoint getNearPoint(BizTravePoint point, String direction);

//    BizTravePoint getPoint(BizTravePoint a, BizTravePoint b, String direction );

    void  doit(BizTravePoint point);
}
