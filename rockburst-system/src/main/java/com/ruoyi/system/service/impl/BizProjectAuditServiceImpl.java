package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.message.WebSocketServer;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.warnPush.WarnPush;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.AlarmHandleHistoryEntity;
import com.ruoyi.system.domain.Entity.AlarmRecordEntity;
import com.ruoyi.system.domain.Entity.PushConfigEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.WarningDTO;
import com.ruoyi.system.domain.dto.largeScreen.SpaceAlarmPushDTO;
import com.ruoyi.system.domain.utils.DrillSpacingWarnUtil;
import com.ruoyi.system.domain.utils.SendMessageUtils;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.AlarmRecordService;
import com.ruoyi.system.service.IBizProjectAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * 工程填报审核记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
@Transactional
public class BizProjectAuditServiceImpl  extends ServiceImpl<BizProjectAuditMapper, BizProjectAudit> implements IBizProjectAuditService
{

    private static final Logger log = LoggerFactory.getLogger(BizProjectAuditServiceImpl.class);

    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private WarnPush warnPush;

    @Resource
    private PushConfigMapper pushConfigMapper;

    @Resource
    private SysUserMapper sysUserMapper;


    @Resource
    private BizDangerAreaMapper bizDangerAreaMapper;
    @Resource
    BizTunnelBarMapper bizTunnelBarMapper;
    @Resource
    BizTravePointMapper bizTravePointMapper;
    @Resource
    CacheDataMapper cacheDataMapper;
    @Resource
    BizDangerLevelMapper bizDangerLevelMapper;
    @Resource
    SysConfigMapper sysConfigMapper;
    @Resource
    private SysDictDataMapper sysDictDataMapper;
    @Resource
    private TunnelMapper tunnelMapper;
    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;
    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private AlarmRecordService alarmRecordService;

    @Resource
    private AlarmHandleHistoryMapper alarmHandleHistoryMapper;

    private static final String ALARM_CONTENT_TEMPLATE = "%s下的%s内，%s号钻孔与%s号钻孔之间的距离为%s米，超过当前卸压计划中间距%s米的要求，发生报警！";



    @Override
    public int audit(Long projectId) {
        BizProjectRecord record = bizProjectRecordMapper.selectById(projectId);
        BizProjectRecord vo = new BizProjectRecord();
        if(record != null && record.getStatus() == BizBaseConstant.FILL_STATUS_TEAM_LOAD){
            vo.setProjectId(projectId).setStatus(BizBaseConstant.FILL_STATUS_TEAM_DOING);
        }
        if(record != null && record.getStatus() == BizBaseConstant.FILL_STATUS_TEAM_OK){
            vo.setProjectId(projectId).setStatus(BizBaseConstant.FILL_STATUS_DEART_DOING);
        }
        bizProjectRecordMapper.updateById(vo);
        return 1;
    }


    @Override
    public int addAudittEAM(BizProjectAudit teamAuditDTO) {
        QueryWrapper<BizProjectAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizProjectAudit::getNo).eq(BizProjectAudit::getProjectId,teamAuditDTO.getProjectId());
        List<BizProjectAudit> list = bizProjectAuditMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            OptionalInt maxNo = list.stream()
                    .mapToInt(BizProjectAudit::getNo)
                    .max();
            if (maxNo.isPresent()) {
                teamAuditDTO.setNo(maxNo.getAsInt()+1);
            }
        }else {
            teamAuditDTO.setNo(1);
        }
        teamAuditDTO.setTag("team");
        bizProjectAuditMapper.insert(teamAuditDTO);
        BizProjectRecord bizProjectRecord = new BizProjectRecord();
        bizProjectRecord.setProjectId(teamAuditDTO.getProjectId())
                .setStatus(getTeamAuditStatus(teamAuditDTO.getStatus()));
        int update = bizProjectRecordMapper.updateById(bizProjectRecord);
        if (update > 0) {
            if (teamAuditDTO.getStatus().equals(1)) {
                List<PushConfigEntity> pushConfigEntities = pushConfigMapper.selectList(new LambdaQueryWrapper<PushConfigEntity>()
                        .eq(PushConfigEntity::getTag, ConstantsInfo.DEPT_AUDIT_PUSH));
                if (ObjectUtil.isNotNull(pushConfigEntities)) {
                    for (PushConfigEntity pushConfigEntity : pushConfigEntities) {
                        String userIdGroup = pushConfigEntity.getUserIdGroup();
                        List<String> userIdGroups = JSON.parseArray(userIdGroup, String.class);
                        List<String> cIds = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                                        .in(SysUser::getUserId, userIdGroups))
                                .stream()
                                .map(SysUser::getCid)
                                .collect(Collectors.toList());
                        warnPush.pushMsg(cIds, "工程填报审核推送通知", teamAuditDTO.getProjectId().toString());
                    }
                }
            }
        }
        return 0;
    }



    @Override
    public int addAuditDeart(BizProjectAudit teamAuditDTO) {

        QueryWrapper<BizProjectAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizProjectAudit::getNo).eq(BizProjectAudit::getProjectId,teamAuditDTO.getProjectId());
        List<BizProjectAudit> list = bizProjectAuditMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            OptionalInt maxNo = list.stream()
                    .mapToInt(BizProjectAudit::getNo)
                    .max();
            if (maxNo.isPresent()) {
                teamAuditDTO.setNo(maxNo.getAsInt()+1);
            }
        }else {
            return 0;
        }
        teamAuditDTO.setTag("deart");
        bizProjectAuditMapper.insert(teamAuditDTO);
        BizProjectRecord bizProjectRecord = new BizProjectRecord();
        bizProjectRecord.setProjectId(teamAuditDTO.getProjectId())
                .setStatus(getDeartAuditStatus(teamAuditDTO.getStatus()));
        int update = bizProjectRecordMapper.updateById(bizProjectRecord);
        if (update > 0) {
            if (teamAuditDTO.getStatus().equals(1)) {
                // 间距判断
                List<WarningDTO> warningDTOS = DrillSpacingWarnUtil.warningLogic(teamAuditDTO.getProjectId(), bizProjectRecordMapper,
                        bizDangerAreaMapper, bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper,
                        cacheDataMapper, bizDangerLevelMapper, sysDictDataMapper, tunnelMapper, bizWorkfaceMapper);
                // 查询已存在的钻孔间距报警记录
                List<AlarmRecordEntity> alarmRecords = alarmRecordMapper.selectList(new LambdaQueryWrapper<AlarmRecordEntity>()
                        .eq(AlarmRecordEntity::getAlarmType, ConstantsInfo.DRILL_SPACE_ALARM));
                List<SpaceAlarmPushDTO> spaceAlarmPushDTOS = new ArrayList<>();
                if (alarmRecords.isEmpty()) {
                    // 没有历史报警记录，批量新增
                    List<AlarmRecordEntity> newRecords = new ArrayList<>();
                    for (WarningDTO warningDTO : warningDTOS) {
                        AlarmRecordEntity record = buildAlarmRecord(warningDTO);
                        newRecords.add(record);
                    }
                    alarmRecordService.saveBatch(newRecords);
                    for (AlarmRecordEntity record : newRecords) {
                        SpaceAlarmPushDTO spaceAlarmPushDTO = convertToPushDTO(record);
                        spaceAlarmPushDTOS.add(spaceAlarmPushDTO);
                    }
                } else {
                    // 存在历史报警记录，逐条处理
                    for (WarningDTO warningDTO : warningDTOS) {
                        AlarmRecordEntity newRecord = buildAlarmRecord(warningDTO);
                        if (warningDTO.isBetweenDrills()) {
                            // 是两个钻孔之间的报警，先关闭旧记录
                            String relatedDrillNum = warningDTO.getRelatedDrillNum();
                            // 查找作为对比钻孔或当前钻孔的历史记录
                            List<AlarmRecordEntity> oldRecords = alarmRecordMapper.selectList(new LambdaQueryWrapper<AlarmRecordEntity>()
                                    .eq(AlarmRecordEntity::getContrastDrillNum, relatedDrillNum)
                                    .or()
                                    .eq(AlarmRecordEntity::getCurrentDrillNum, relatedDrillNum));

                            if (!oldRecords.isEmpty()) {
                                // 关闭旧记录
                                for (AlarmRecordEntity oldRecord : oldRecords) {
                                    alarmRecordMapper.update(oldRecord, new LambdaUpdateWrapper<AlarmRecordEntity>()
                                            .eq(AlarmRecordEntity::getAlarmId, oldRecord.getAlarmId())
                                            .set(AlarmRecordEntity::getEndTime, System.currentTimeMillis())
                                            .set(AlarmRecordEntity::getAlarmStatus, ConstantsInfo.ALARM_END));
                                    // 加入处理历史
                                    AlarmHandleHistoryEntity historyEntity = new AlarmHandleHistoryEntity()
                                            .setAlarmId(oldRecord.getAlarmId())
                                            .setHandlePerson(ConstantsInfo.ALARM_SYSTEM)
                                            .setHandleTime(System.currentTimeMillis())
                                            .setOperate(ConstantsInfo.TURN_OFF_ALARM)
                                            .setRemarks(ConstantsInfo.REMARKS_SYSTEM);
                                    alarmHandleHistoryMapper.insert(historyEntity);
                                }
                            }
                            // 新增当前记录
                            alarmRecordMapper.insert(newRecord);
                            SpaceAlarmPushDTO spaceAlarmPushDTO = convertToPushDTO(newRecord);
                            spaceAlarmPushDTOS.add(spaceAlarmPushDTO);

                        } else {
                            // 非两个钻孔之间问题，直接新增
                            alarmRecordMapper.insert(newRecord);
                            SpaceAlarmPushDTO spaceAlarmPushDTO = convertToPushDTO(newRecord);
                            spaceAlarmPushDTOS.add(spaceAlarmPushDTO);
                        }
                    }
                }
                // 异步推送报警通知(大屏webSocket推送)
                asyncSendAlarmNotifications(spaceAlarmPushDTOS);
                // APP推送报警通知(极光推送)
                List<PushConfigEntity> pushConfigEntities = pushConfigMapper.selectList(new LambdaQueryWrapper<PushConfigEntity>()
                        .eq(PushConfigEntity::getTag, ConstantsInfo.DEPT_AUDIT_PUSH));
                if (ObjectUtil.isNotNull(pushConfigEntities)) {
                    for (PushConfigEntity pushConfigEntity : pushConfigEntities) {
                        String userIdGroup = pushConfigEntity.getUserIdGroup();
                        List<String> userIdGroups = JSON.parseArray(userIdGroup, String.class);
                        List<String> cIds = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                                        .in(SysUser::getUserId, userIdGroups))
                                .stream()
                                .map(SysUser::getCid)
                                .collect(Collectors.toList());
                        warnPush.pushMsg(cIds, "钻孔间距预警推送通知", spaceAlarmPushDTOS.toString());
                    }
                }
            }
        }
        return 1;
    }


    int getDeartAuditStatus(Integer status){
        if(status == 1){
            return BizBaseConstant.FILL_STATUS_DEART_OK;
        }
        if(status == 0){
            return BizBaseConstant.FILL_STATUS_DEART_BACK;
        }
        return BizBaseConstant.FILL_STATUS_DEART_OK;
    }

    int getTeamAuditStatus(Integer status){
        if(status == 1){
            return BizBaseConstant.FILL_STATUS_TEAM_OK;
        }
        if(status == 0){
            return BizBaseConstant.FILL_STATUS_TEAM_BACK;
        }
        return BizBaseConstant.FILL_STATUS_TEAM_OK;
    }


    /**
     * 构造报警记录
     */
    private AlarmRecordEntity buildAlarmRecord(WarningDTO warningDTO) {
        AlarmRecordEntity record = new AlarmRecordEntity();
        record.setAlarmType(ConstantsInfo.DRILL_SPACE_ALARM);
        record.setProjectId(warningDTO.getCurrentProjectId());
        record.setContrastDrillNum(warningDTO.getCurrentDrillNum());
        record.setCurrentDrillNum(warningDTO.getRelatedDrillNum());
        record.setSpaced(warningDTO.getSpaced());
        record.setActualDistance(warningDTO.getActualDistance());
        // 构建报警内容
        String alarmContent = String.format(ALARM_CONTENT_TEMPLATE,
                warningDTO.getWorkFaceName(),
                warningDTO.getTunnelName(),
                warningDTO.getCurrentDrillNum(),
                warningDTO.getRelatedDrillNum(),
                warningDTO.getActualDistance(),
                warningDTO.getSpaced());
        record.setAlarmContent(alarmContent);
        record.setAlarmStatus(ConstantsInfo.ALARM_IN);
        record.setHandleStatus(ConstantsInfo.UNTREATED);
        record.setStartTime(System.currentTimeMillis());
        return record;
    }
    /**
     * 异步发送报警通知
     */
    @Async
    public void asyncSendAlarmNotifications(List<SpaceAlarmPushDTO> alarmPushDTOS) {
        try {
            String message = SendMessageUtils.sendMessage(alarmPushDTOS);
            WebSocketServer.sendInfoAll(message);
            log.info("WebSocket推送钻孔间距报警成功，数量: {}", alarmPushDTOS.size());
        } catch (IOException e) {
            log.error("WebSocket推送钻孔间距报警失败，影响数据量: {}", alarmPushDTOS.size(), e);
        }
    }
    /**
     * 转换为推送DTO
     */
    private SpaceAlarmPushDTO convertToPushDTO(AlarmRecordEntity alarmRecord) {
        SpaceAlarmPushDTO spaceAlarmPushDTO = new SpaceAlarmPushDTO();
        spaceAlarmPushDTO.setAlarmId(alarmRecord.getAlarmId());
        spaceAlarmPushDTO.setAlarmType(ConstantsInfo.DRILL_SPACE_ALARM);
        spaceAlarmPushDTO.setAlarmTime(alarmRecord.getStartTime());
        spaceAlarmPushDTO.setAlarmContent(alarmRecord.getAlarmContent());
        spaceAlarmPushDTO.setCurrentProjectId(alarmRecord.getProjectId());
        spaceAlarmPushDTO.setCurrentDrillNum(alarmRecord.getCurrentDrillNum());
        spaceAlarmPushDTO.setContrastDrillNum(alarmRecord.getContrastDrillNum());
        spaceAlarmPushDTO.setSpaced(alarmRecord.getSpaced());
        spaceAlarmPushDTO.setActualDistance(alarmRecord.getActualDistance());
        String workFaceName = getWorkFaceName(alarmRecord.getProjectId());
        spaceAlarmPushDTO.setWorkFaceName(workFaceName);
        spaceAlarmPushDTO.setTunnelName(getTunnelName(alarmRecord.getProjectId()));
        return spaceAlarmPushDTO;
    }
    private String getWorkFaceName(Long projectId){
        String workfaceName = "";
        Long workFaceId = null;
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        workFaceId = bizProjectRecord.getWorkfaceId();
        QueryWrapper<BizWorkface> workfaceQueryWrapper = new QueryWrapper<>();
        workfaceQueryWrapper.lambda().eq(BizWorkface::getWorkfaceId, workFaceId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(workfaceQueryWrapper);
        if (ObjectUtil.isNotNull(bizWorkface)) {
            workfaceName = bizWorkface.getWorkfaceName();
        }
        return workfaceName;
    }

    private String getTunnelName(Long projectId){
        String tunnelName = "";
        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda().eq(TunnelEntity::getTunnelId, projectId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(tunnelQueryWrapper);
        if (ObjectUtil.isNotNull(tunnelEntity)) {
            tunnelName = tunnelEntity.getTunnelName();
        }
        return tunnelName;
    }
}
