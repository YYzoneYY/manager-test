package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ExcavationRecordEntity;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationRecordDTO;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningRecordDTO;
import com.ruoyi.system.mapper.ExcavationRecordMapper;
import com.ruoyi.system.mapper.MiningRecordMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ExcavationRecordService;
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
public class ExcavationRecordServiceImpl extends ServiceImpl<ExcavationRecordMapper, ExcavationRecordEntity> implements ExcavationRecordService {

    @Resource
    private ExcavationRecordMapper excavationRecordMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public ExcavationFootageDTO insertExcavationRecord(ExcavationFootageDTO excavationFootageDTO) {
        ExcavationRecordEntity excavationRecordEntity = new ExcavationRecordEntity();
        BeanUtils.copyBeanProp(excavationRecordEntity, excavationFootageDTO);
        excavationRecordMapper.insert(excavationRecordEntity);
        return excavationFootageDTO;
    }

    @Override
    public List<ExcavationRecordDTO> queryByExcavationRecordId(Long excavationFootageId) {
        List<ExcavationRecordDTO> excavationRecordDTOS = excavationRecordMapper.queryByExcavationRecordId(excavationFootageId);
        if (ListUtils.isNotNull(excavationRecordDTOS)) {
            excavationRecordDTOS.forEach(excavationRecordDTO -> {
                excavationRecordDTO.setExcavationTimeFrm(DateUtils.getDateStrByTime(excavationRecordDTO.getExcavationTime()));
                excavationRecordDTO.setUpdateTimeFrm(excavationRecordDTO.getUpdateTime() == null ? null : DateUtils.getDateStrByTime(excavationRecordDTO.getUpdateTime()));
                String name = sysUserMapper.selectNameById(excavationRecordDTO.getUpdateBy());
                excavationRecordDTO.setCreateByFmt(name);
            });
        }
        return excavationRecordDTOS;

    }
}