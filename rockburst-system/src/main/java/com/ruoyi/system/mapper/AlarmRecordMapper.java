package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.AlarmRecordEntity;
import com.ruoyi.system.domain.dto.largeScreen.AlarmRecordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/10
 * @description:
 */

@Mapper
public interface AlarmRecordMapper extends BaseMapper<AlarmRecordEntity> {

    Integer selectMaxNumber(@Param("planId") Long planId,
                            @Param("alarmThreshold") BigDecimal alarmThreshold);

    Integer selectMaxNumberT(@Param("projectId") Long projectId);


    List<AlarmRecordDTO> selectAlarmRecord(@Param("alarmType") String alarmType,
                                           @Param("startTime") Long startTime,
                                           @Param("endTime") Long endTime);
}