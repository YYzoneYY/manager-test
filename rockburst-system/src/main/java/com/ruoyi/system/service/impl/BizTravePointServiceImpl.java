package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.dto.BizMineDto;
import com.ruoyi.system.mapper.BizMineMapper;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.service.IBizMineService;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizTravePointServiceImpl extends ServiceImpl<BizTravePointMapper, BizTravePoint> implements IBizTravePointService
{

}
