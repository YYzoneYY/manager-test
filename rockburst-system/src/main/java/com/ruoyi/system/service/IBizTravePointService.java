package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizTravePoint;
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


    List<BizTravePoint>  getQyPoint(Long workfaceId);

    void  doit(BizTravePoint point);
}
