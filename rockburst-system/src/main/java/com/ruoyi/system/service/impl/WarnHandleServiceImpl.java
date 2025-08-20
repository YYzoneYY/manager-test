package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.WarnHandleEntity;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.dto.actual.WarnHandleDTO;
import com.ruoyi.system.mapper.WarnHandleMapper;
import com.ruoyi.system.service.WarnHandleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */

@Transactional
@Service
public class WarnHandleServiceImpl extends ServiceImpl<WarnHandleMapper, WarnHandleEntity> implements WarnHandleService {

    @Resource
    private WarnHandleMapper warnHandleMapper;

    @Override
    public int addWarnHandle(String warnInstanceNum, WarnHandleDTO warnHandleDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(warnInstanceNum)) {
            throw new RuntimeException("参数错误,警情编号不能为空！");
        }
        if (ObjectUtil.isNull(warnHandleDTO)) {
            throw new RuntimeException("参数错误,不能为空！");
        }
        WarnHandleEntity warnHandleEntity = new WarnHandleEntity();
        BeanUtils.copyProperties(warnHandleDTO, warnHandleEntity);
        warnHandleEntity.setWarnInstanceNum(warnInstanceNum);
        warnHandleEntity.setMineId(mineId);
        warnHandleEntity.setCreateBy(SecurityUtils.getUserId());
        warnHandleEntity.setCreateTime(System.currentTimeMillis());
        flag = warnHandleMapper.insert(warnHandleEntity);
        if (flag <= 0) {
            throw new RuntimeException("处理失败,请联系管理员");
        }
        return flag;
    }
}