package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.MiningRecordNewEntity;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.domain.dto.MiningRecordNewDTO;
import com.ruoyi.system.mapper.MiningRecordNewMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.MiningRecordNewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@Service
@Transactional
public class MiningRecordNewServiceImpl extends ServiceImpl<MiningRecordNewMapper, MiningRecordNewEntity> implements MiningRecordNewService {

    @Resource
    private MiningRecordNewMapper miningRecordNewMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public int insertMiningRecordNew(MiningFootageNewDTO miningFootageNewDTO) {
        int flag = 0;
        MiningRecordNewEntity miningRecordNewEntity = new MiningRecordNewEntity();
        BeanUtils.copyProperties(miningFootageNewDTO,miningRecordNewEntity);
        flag = miningRecordNewMapper.insert(miningRecordNewEntity);
        return flag;
    }

    @Override
    public List<MiningRecordNewDTO> queryByMiningRecordId(Long miningFootageId) {
        List<MiningRecordNewDTO> miningRecordDTOS = miningRecordNewMapper.queryByMiningFootageId(miningFootageId);
        if (ListUtils.isNotNull(miningRecordDTOS)) {
            miningRecordDTOS.forEach(miningRecordDTO -> {
                miningRecordDTO.setMiningTimeFrm(DateUtils.getDateStrByTime(miningRecordDTO.getMiningTime()));
                miningRecordDTO.setUpdateTimeFrm(DateUtils.getDateStrByTime(miningRecordDTO.getUpdateTime()));
                String name = sysUserMapper.selectNameById(miningRecordDTO.getUpdateBy());
                miningRecordDTO.setCreateByFmt(name);
            });
        }
        return miningRecordDTOS;
    }
}