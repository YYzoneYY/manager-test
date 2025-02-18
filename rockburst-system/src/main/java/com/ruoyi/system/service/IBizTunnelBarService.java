package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.dto.BizTunnelBarDto;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizTunnelBarService extends IService<BizTunnelBar>
{


    public BizTunnelBar selectEntityById(Long id);


    public MPage<BizTunnelBar> selectEntityList(BizTunnelBarDto dto, Pagination pagination);


    public int insertEntity(BizTunnelBarDto dto);


    public int updateEntity(BizTunnelBarDto dto);





}
