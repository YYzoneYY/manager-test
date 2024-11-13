package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningRecordDTO;
import com.ruoyi.system.mapper.MiningRecordMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.MiningRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Service
@Transactional
public class MiningRecordServiceImpl extends ServiceImpl<MiningRecordMapper, MiningRecordEntity> implements MiningRecordService {

    @Resource
    private MiningRecordMapper miningRecordMapper;
    @Resource SysUserMapper sysUserMapper;

    @Override
    public MiningFootageDTO insertMiningRecord(MiningFootageDTO miningFootageDTO) {
        MiningRecordEntity miningRecordEntity = new MiningRecordEntity();
        BeanUtils.copyProperties(miningFootageDTO,miningRecordEntity);
        miningRecordMapper.insert(miningRecordEntity);
        return miningFootageDTO;
    }

    @Override
    public List<MiningRecordDTO> queryByMiningRecordId(Long miningRecordId) {
        List<MiningRecordDTO> miningRecordDTOS = miningRecordMapper.queryByMiningRecordId(miningRecordId);
        miningRecordDTOS.forEach(miningRecordDTO -> {
            miningRecordDTO.setMiningTimeFrm(DateUtils.getDateStrByTime(miningRecordDTO.getMiningTime()));
            miningRecordDTO.setUpdateTimeFrm(DateUtils.getDateStrByTime(miningRecordDTO.getUpdateTime()));
            String name = sysUserMapper.selectNameById(miningRecordDTO.getUpdateBy());
            miningRecordDTO.setCreateByFmt(name);
        });
        return miningRecordDTOS;
    }
}