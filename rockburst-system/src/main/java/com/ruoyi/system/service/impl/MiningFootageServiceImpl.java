package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.MiningFootageMapper;
import com.ruoyi.system.service.MiningFootageService;
import com.ruoyi.system.service.MiningRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Service
@Transactional
public class MiningFootageServiceImpl extends ServiceImpl<MiningFootageMapper, MiningFootageEntity> implements MiningFootageService{

    @Resource MiningFootageMapper miningFootageMapper;
    @Resource BizWorkfaceMapper bizWorkfaceMapper;
    @Resource MiningRecordService miningRecordService;

    /**
     * 新增回采进尺
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public MiningFootageDTO insertMiningFootage(MiningFootageDTO miningFootageDTO) {
        if (miningFootageDTO.getMiningPace().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("输入的回采进尺不能或小于0");
        }
        BizWorkface bizWorkface = bizWorkfaceMapper.selectById(miningFootageDTO.getWorkfaceId());
        if (ObjectUtil.isEmpty(bizWorkface)) {
            throw new RuntimeException("选择的工作面不存在，请重新选择");
        }
        BigDecimal surplusFaceTotal = surplusFaceTotal(miningFootageDTO.getWorkfaceId(), bizWorkface.getStrikeLength(), BigDecimal.ZERO);
        if (miningFootageDTO.getMiningPace().compareTo(surplusFaceTotal) > 0) {
            throw new RuntimeException("回采进尺不能大于剩余工作面长度" + surplusFaceTotal + "米");
        }
        if (miningFootageDTO.getMiningPace().compareTo(BigDecimal.ZERO) == 0) {
            miningFootageDTO.setFlag(MiningFootageEnum.Not_FILLED_IN.getIndex());
        } else {
            miningFootageDTO.setFlag(MiningFootageEnum.NORMAL.getIndex());
        }
        long ts = System.currentTimeMillis();
        miningFootageDTO.setCreateTime(ts);
        miningFootageDTO.setUpdateTime(ts);
        miningFootageDTO.setCreateBy(1L);
        miningFootageDTO.setUpdateBy(1L);
        MiningFootageEntity miningFootageEntity = new MiningFootageEntity();
        BeanUtils.copyProperties(miningFootageDTO,miningFootageEntity);
        miningFootageMapper.insert(miningFootageEntity);
        BeanUtils.copyProperties(miningFootageEntity,miningFootageDTO);
        return miningFootageDTO;
    }

    /**
     * 回采进尺修改
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public MiningFootageEntity updateMiningFootage(MiningFootageDTO miningFootageDTO) {
        if (ObjectUtil.isEmpty(miningFootageDTO.getMiningFootageId())) {
            throw new RuntimeException("回采进尺id不能为空");
        }
        MiningFootageEntity miningFootageEntity = miningFootageMapper.selectById(miningFootageDTO.getMiningFootageId());
        if (ObjectUtil.isNotEmpty(miningFootageEntity)) {
            throw new RuntimeException("回采进尺不存在");
        }
        BizWorkface bizWorkface = bizWorkfaceMapper.selectById(miningFootageEntity.getWorkfaceId());
        BigDecimal surplusFaceTotal = surplusFaceTotal(miningFootageDTO.getWorkfaceId(), bizWorkface.getStrikeLength(), BigDecimal.ZERO);
        if (miningFootageDTO.getMiningPace().compareTo(surplusFaceTotal) > 0) {
            throw new RuntimeException("回采进尺不能大于剩余工作面长度" + surplusFaceTotal + "米");
        }
        miningFootageEntity.setFlag(MiningFootageEnum.REVISE.getIndex());
        miningFootageDTO.setUpdateTime(System.currentTimeMillis());
        miningFootageDTO.setUpdateBy(1L);
        boolean b = this.updateById(miningFootageEntity);
        if (b) {
            miningFootageDTO.setFlag(MiningFootageEnum.REVISE.getIndex());
            miningFootageDTO.setWorkfaceId(miningFootageEntity.getWorkfaceId());
            miningFootageDTO.setMiningTime(miningFootageEntity.getMiningTime());
            Long ts = System.currentTimeMillis();
            miningFootageDTO.setCreateTime(ts);
            miningFootageDTO.setUpdateTime(ts);
            miningFootageDTO.setCreateBy(1L);
            miningFootageDTO.setUpdateBy(1L);
            miningRecordService.insertMiningRecord(miningFootageDTO);
        }
        return miningFootageDTO;
    }

    /**
     *
     * @param workfaceId 工作面id
     * @param faceLength 工作面长度
     * @param currentLength 当前这条数据的源数据开采进尺
     * @return 返回结果
     */
    private BigDecimal surplusFaceTotal(Long workfaceId, BigDecimal faceLength, BigDecimal currentLength) {
        //已开采总长度
        BigDecimal mineTotalLength = miningFootageMapper.minedLength(workfaceId);
        BigDecimal subtractFaceLength;
        //判断工作面是否有值
        if (mineTotalLength != null) {
            subtractFaceLength = faceLength.subtract(mineTotalLength).add(currentLength);
        } else {
            subtractFaceLength = faceLength.subtract(currentLength);
        }
        return subtractFaceLength;
    }
}