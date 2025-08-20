package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ResponseOperateEntity;
import com.ruoyi.system.domain.dto.actual.ResponseOperateDTO;
import com.ruoyi.system.mapper.ResponseOperateMapper;
import com.ruoyi.system.service.ResponseOperateService;
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
public class ResponseOperateServiceImpl extends ServiceImpl<ResponseOperateMapper, ResponseOperateEntity> implements ResponseOperateService {

    @Resource
    private ResponseOperateMapper responseOperateMapper;

    @Override
    public int addResponseOperate(String warnInstanceNum, ResponseOperateDTO responseOperateDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(warnInstanceNum)) {
            throw new RuntimeException("参数错误,警情编号不能为空！");
        }
        if (ObjectUtil.isNull(responseOperateDTO)) {
            throw new RuntimeException("参数错误,不能为空！");
        }
        ResponseOperateEntity responseOperateEntity = new ResponseOperateEntity();
        BeanUtils.copyProperties(responseOperateDTO, responseOperateEntity);
        responseOperateEntity.setWarnInstanceNum(warnInstanceNum);
        responseOperateEntity.setMineId(mineId);
        responseOperateEntity.setCreateBy(SecurityUtils.getUserId());
        responseOperateEntity.setCreateTime(System.currentTimeMillis());
        flag = responseOperateMapper.insert(responseOperateEntity);
        if (flag <= 0) {
            throw new RuntimeException("处理失败,请联系管理员");
        }
        return flag;
    }
}