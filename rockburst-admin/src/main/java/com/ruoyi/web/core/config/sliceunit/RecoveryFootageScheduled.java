package com.ruoyi.web.core.config.sliceunit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.Entity.MiningEntity;
import com.ruoyi.system.domain.Entity.RuleConfigEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.mapper.RuleConfigMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.MiningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/6/21
 * @description:
 */

@Lazy(value = false)
@Component
@EnableScheduling
public class RecoveryFootageScheduled implements SchedulingConfigurer {

    @Resource
    private MiningService miningService;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private RuleConfigMapper ruleConfigMapper;

    private static String cron;

    //生成的日志
    private static final Logger recover_footage_logger = LoggerFactory.getLogger("recover-footage");

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("进尺新增，动态任务正在运行 ...");
                //任务逻辑代码部分.
                System.out.println("================定时任务开始=================");
                //获取当前日期
                Date date = new Date();
                //将时间格式化成yyyy-MM-dd HH:mm:ss的格式
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //创建Calendar实例
                Calendar cal = Calendar.getInstance();
                //设置当前时间
                cal.setTime(date);
                //减去一天的方法：
                cal.add(Calendar.DATE, -1);
                String format1 = format.format(cal.getTime());
                String format2 = format.format(date);
                System.out.println(format1);
                System.out.println(format2);
                try {
                    Long startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format1).getTime();
                    Long endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format2).getTime();

                    List<MiningEntity> miningEntities = miningService.queryCurrentDay(startTime, endTime);
                    List<TunnelEntity> tunnelEntities = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                            .eq(TunnelEntity::getTunnelStatus, "2")); // //状态为掘进完成的所有巷道
                    if (CollectionUtils.isEmpty(miningEntities)) {
                        if (ListUtils.isNotNull(tunnelEntities)) {
                            tunnelEntities.forEach(tunnelEntity -> {
                                MiningFootageNewDTO miningFootageNewDTO = new MiningFootageNewDTO();
                                miningFootageNewDTO.setFlag(MiningFootageEnum.Not_FILLED_IN.getIndex()); // 未填写
                                long ts = System.currentTimeMillis();
                                miningFootageNewDTO.setCreateTime(ts); // 精确到毫秒
                                Date date1 = new Date(ts);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置当前时间格式
                                cal.setTime(date1);
                                cal.add(Calendar.DATE, -1); //获取前一天时间
                                String fm = simpleDateFormat.format(cal.getTime());
                                try {
                                    Date fmDate = simpleDateFormat.parse(fm);
                                    miningFootageNewDTO.setMiningTime(fmDate.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                miningFootageNewDTO.setMiningPace(BigDecimal.ZERO);
                                miningFootageNewDTO.setTunnelId(tunnelEntity.getTunnelId());
                                miningFootageNewDTO.setWorkFaceId(tunnelEntity.getWorkFaceId());
                                miningService.insertMiningFootage(miningFootageNewDTO);
                                System.out.println("当天所有巷道未查到回采进尺数据" + tunnelEntity.getTunnelId() + "新增成功");
                                recover_footage_logger.info("当天所有巷道未查到回采进尺数据" + tunnelEntity.getTunnelId() + "新增成功");
                            });
                        }
                    }
                    List<Long> miningTunnelIds = miningEntities.stream().map(MiningEntity::getTunnelId).collect(Collectors.toList());
                    if (!miningTunnelIds.isEmpty()) {
                        List<Long> tunnelIdList = tunnelEntities.stream().map(TunnelEntity::getTunnelId).collect(Collectors.toList());
                        //当天没有新增巷道的id集合
                        List<Long> noTunnelId = tunnelIdList.stream().filter(e -> {
                            return !miningTunnelIds.contains(e);
                        }).collect(Collectors.toList());
                        if (!noTunnelId.isEmpty()) {
                            noTunnelId.forEach(tunnelId -> {
                                MiningFootageNewDTO miningFootageNewDTO = new MiningFootageNewDTO();
                                miningFootageNewDTO.setTunnelId(tunnelId);
                                TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                                        .eq(TunnelEntity::getTunnelId, tunnelId));
                                miningFootageNewDTO.setWorkFaceId(tunnelEntity.getWorkFaceId());
                                miningFootageNewDTO.setFlag(MiningFootageEnum.Not_FILLED_IN.getIndex()); //未填写
                                long ts = System.currentTimeMillis();
                                miningFootageNewDTO.setCreateTime(ts);//精确到毫秒
                                Date date1 = new Date(ts);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置当前时间格式
                                cal.setTime(date1);
                                cal.add(Calendar.DATE, -1); //获取前一天时间
                                String fm = simpleDateFormat.format(cal.getTime());
                                try {
                                    Date fmDate = simpleDateFormat.parse(fm);
                                    miningFootageNewDTO.setMiningTime(fmDate.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                miningFootageNewDTO.setMiningPace(BigDecimal.ZERO);
                                miningFootageNewDTO.setTunnelId(tunnelId);
                                miningService.insertMiningFootage(miningFootageNewDTO);
                                System.out.println("当天巷道未查到回采进尺数据" + tunnelEntity.getTunnelId() + "新增成功");
                                recover_footage_logger.info("当天巷道未查到回采进尺数据" + tunnelEntity.getTunnelId() + "新增成功");
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    recover_footage_logger.info(e.getMessage() + "=====" + e.toString() + "====" + e.getStackTrace().toString());

                }
                System.out.println("================================定时任务结束=========================");
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                RuleConfigEntity ruleConfigEntity = ruleConfigMapper.selectOne(null);
                String hourTime="0";
                String minuteTime="0";
                if (ruleConfigEntity != null) {
                    String ruleTime = ruleConfigEntity.getStartTime();
                    if(StringUtils.isNotEmpty(ruleTime) && ruleTime.contains(":")){
                        String[] timeParts = ruleTime.split(":");
                        if (timeParts.length >= 2) {
                            hourTime = timeParts[0];
                            minuteTime = timeParts[1];
                        }
                    }
                }
                // 修复Cron表达式，确保它有6个字段：秒 分 时 日 月 周
                cron = "0 " + minuteTime + " " + hourTime + " " +
                        "*" + " " +  "*" + " " + "?";
                CronTrigger trigger = new CronTrigger(cron);
                Date nextExecutionTime = trigger.nextExecutionTime(triggerContext);
                return nextExecutionTime;
            }
        });
    }

}