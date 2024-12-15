package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.enums.WorkFaceType;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningSelectDTO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.MiningFootageMapper;
import com.ruoyi.system.service.MiningFootageService;
import com.ruoyi.system.service.MiningRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        BizWorkface bizWorkface = bizWorkfaceMapper.selectById(miningFootageDTO.getWorkFaceId());
        if (ObjectUtil.isEmpty(bizWorkface)) {
            throw new RuntimeException("选择的工作面不存在，请重新选择");
        }
        BigDecimal surplusFaceTotal = surplusFaceTotal(miningFootageDTO.getWorkFaceId(), bizWorkface.getStrikeLength(), BigDecimal.ZERO);
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
        miningFootageDTO.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        MiningFootageEntity miningFootageEntity = new MiningFootageEntity();
        BeanUtils.copyProperties(miningFootageDTO,miningFootageEntity);
        int insert = miningFootageMapper.insert(miningFootageEntity);
        if (insert > 0) {
            BeanUtils.copyProperties(miningFootageEntity,miningFootageDTO);
        }
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
        if (ObjectUtil.isEmpty(miningFootageEntity)) {
            throw new RuntimeException("回采进尺不存在");
        }
        BizWorkface bizWorkface = bizWorkfaceMapper.selectById(miningFootageEntity.getWorkFaceId());
        BigDecimal surplusFaceTotal = surplusFaceTotal(miningFootageDTO.getWorkFaceId(), bizWorkface.getStrikeLength(), BigDecimal.ZERO);
        if (miningFootageDTO.getMiningPace().compareTo(surplusFaceTotal) > 0) {
            throw new RuntimeException("回采进尺不能大于剩余工作面长度" + surplusFaceTotal + "米");
        }
        miningFootageEntity.setFlag(MiningFootageEnum.REVISE.getIndex());
        miningFootageDTO.setUpdateTime(System.currentTimeMillis());
        miningFootageDTO.setUpdateBy(1L);
        boolean b = this.updateById(miningFootageEntity);
        if (b) {
            miningFootageDTO.setFlag(MiningFootageEnum.REVISE.getIndex());
            miningFootageDTO.setWorkFaceId(miningFootageEntity.getWorkFaceId());
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
     * 擦除
     * @param miningFootageDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int clear(MiningFootageDTO miningFootageDTO) {
        if (ObjectUtil.isEmpty(miningFootageDTO.getMiningFootageId())) {
            throw new RuntimeException("请选择数据");
        }
        MiningFootageEntity miningFootageEntity = miningFootageMapper.selectById(miningFootageDTO.getMiningFootageId());
        if (ObjectUtil.isEmpty(miningFootageEntity)) {
            throw new RuntimeException("未找到数据");
        }
        LambdaUpdateWrapper<MiningFootageEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MiningFootageEntity::getMiningFootageId,miningFootageDTO.getMiningFootageId())
                .set(MiningFootageEntity::getMiningPace, 0)
                .set(MiningFootageEntity::getFlag, MiningFootageEnum.ERASE.getIndex());//3-标识擦除
        int update = miningFootageMapper.update(null, updateWrapper);
        if (update > 0) {
            miningFootageDTO.setWorkFaceId(miningFootageEntity.getWorkFaceId());
            miningFootageDTO.setMiningTime(miningFootageEntity.getMiningTime());
            miningFootageDTO.setFlag(MiningFootageEnum.ERASE.getIndex()); //3-标识擦除
            miningFootageDTO.setMiningPaceEdit(BigDecimal.ZERO);
            miningFootageDTO.setUpdateTime(System.currentTimeMillis());
            miningRecordService.insertMiningRecord(miningFootageDTO);
        }
        return update;
    }

    /**
     * 分页查询
     * @param miningSelectDTO 参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(MiningSelectDTO miningSelectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<MiningFootageEntity> miningFootageEntities = miningFootageMapper.selectList(new LambdaQueryWrapper<MiningFootageEntity>()
                .eq(MiningFootageEntity::getWorkFaceId, miningSelectDTO.getWorkFaceId()));
        if (miningFootageEntities.isEmpty()){
            return null;
        }
        Page<MiningFootageDTO> page = miningFootageMapper.selectMiningFootageByPage(miningSelectDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            List<Long> collect = page.getResult().stream().collect(Collectors.groupingBy(MiningFootageEntity::getMiningTime, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(entry -> entry.getKey())
                    .collect(Collectors.toList());

            page.getResult().forEach(miningFootageDTO -> {
                BigDecimal bigDecimal = miningPaceSum(miningFootageDTO.getWorkFaceId(), miningFootageDTO.getMiningTime());
                miningFootageDTO.setMiningPaceSum(bigDecimal);
                collect.forEach(c -> {
                    if (miningFootageDTO.getMiningTime().equals(c)) {
                        miningFootageDTO.setFlag(MiningFootageEnum.SAME_TIME.getIndex());
                    }
                });
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 查询是否有时间相同
     * @param miningTime 时间
     * @param workfaceId 工作面id
     * @return 返回结果
     */
    @Override
    public String queryByTime(Long miningTime, Long workfaceId) {
        LambdaQueryWrapper<MiningFootageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MiningFootageEntity::getMiningTime, miningTime)
                .eq(MiningFootageEntity::getWorkFaceId, workfaceId);
        List<MiningFootageEntity> miningFootageEntities = miningFootageMapper.selectList(queryWrapper);
        if (!miningFootageEntities.isEmpty()) {
            return MiningFootageEnum.SAME_TIME.getIndex();
        }
        return MiningFootageEnum.DIFFERENT_TIME.getIndex();
    }

    /**
     * 获取剩余工作面总长度
     * @param workfaceId 工作面id
     * @return 返回结果
     */
    @Override
    public BigDecimal getSurplusLength(Long workfaceId) {
        Long selectCount = miningFootageMapper.selectCount(new LambdaQueryWrapper<MiningFootageEntity>()
                .eq(MiningFootageEntity::getWorkFaceId, workfaceId));
        if (selectCount == 0) {
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, workfaceId)
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizWorkface)) {
                throw new RuntimeException("未找到工作面信息");
            }
            return bizWorkface.getStrikeLength();
        }
        BigDecimal mineTotalLength = miningFootageMapper.minedLength(workfaceId); //已开采的的工作长度
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, workfaceId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotEmpty(bizWorkface)) {
            BigDecimal strikeLength = bizWorkface.getStrikeLength(); //工作面的总长度
            BigDecimal subtract = strikeLength.subtract(mineTotalLength);//剩余可开采的工作面长度

            BizWorkface workface = new BizWorkface();
            BeanUtils.copyProperties(bizWorkface, workface);
            if (subtract.equals(BigDecimal.ZERO) || subtract.compareTo(BigDecimal.ZERO) < 0) {
                workface.setStatus(WorkFaceType.kcw.getCode());
            } else {
                workface.setStatus(WorkFaceType.kcz.getCode());
            }
            bizWorkfaceMapper.updateById(workface);
            return subtract;
        } else {
            throw new RuntimeException("未找到工作面信息");
        }
    }

    /**
     *按照时间查询每次开采进度之和
     * @param workfaceId 工作面id
     * @param time 时间
     * @return 返回结果
     */
    public BigDecimal miningPaceSum(Long workfaceId, Long time) {
        return miningFootageMapper.miningPaceSum(workfaceId, time);
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