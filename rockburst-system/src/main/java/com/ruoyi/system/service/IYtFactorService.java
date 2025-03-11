package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.YtFactor;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IYtFactorService extends IService<YtFactor>
{
    public MPage<YtFactor> selectEntityList(YtFactor dto, Pagination pagination);







}
