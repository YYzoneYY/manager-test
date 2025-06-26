package com.ruoyi.web.controller.business;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.message.WebSocketServer;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.quartz.task.impl.RyTaskServiceImpl;
import com.ruoyi.system.domain.dto.largeScreen.*;
import com.ruoyi.system.domain.utils.SendMessageUtils;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.LargeScreenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/10
 * @description:
 */

@Api(tags = "3D大屏")
@RestController
@RequestMapping(value = "/largeScreen")
public class LargeScreenController {

    private static final Logger log = LoggerFactory.getLogger(LargeScreenController.class);

    @Resource
    private LargeScreenService largeScreenService;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;

    @ApiOperation(value = "获取施工工程列表", notes = "获取施工工程列表")
    @PostMapping(value = "/obtainProject")
    public R<List<ProjectDTO>> obtainProject(@ApiParam(name = "tag", value = "1:已审核 2:未审核", required = true) @RequestParam String tag,
                                             @RequestBody Select1DTO select1DTO) {
        return R.ok(this.largeScreenService.obtainProject(tag, select1DTO));
    }

    @ApiOperation(value = "获取施工类型分类统计", notes = "获取施工类型分类统计")
    @GetMapping(value = "/obtainProjectType")
    public R<List<ProjectTypeDTO>> obtainProjectType(@ApiParam(name = "startTime", value = "开始时间") @RequestParam(required = false) Long startTime,
                                                     @ApiParam(name = "endTime", value = "结束时间") @RequestParam(required = false) Long endTime) {
        return R.ok(this.largeScreenService.obtainProjectType(new Date(startTime), new Date(endTime)));
    }

    @ApiOperation(value = "获取施工计划统计", notes = "获取施工计划统计")
    @GetMapping(value = "/obtainPlanCount")
    public R<List<PlanCountDTO>> obtainPlanCount() {
        return R.ok(this.largeScreenService.obtainPlanCount());
    }

    @ApiOperation(value = "获取施工钻孔树", notes = "获取施工钻孔树")
    @GetMapping(value = "/obtainProjectTree")
    public R<List<SimpleTreeDTO>> obtainProjectTree() {
        return R.ok(this.largeScreenService.obtainProjectTree());
    }

    @ApiOperation(value = "获取视频地址", notes = "获取视频地址")
    @GetMapping(value = "/obtainUrl")
    public R<DataDTO> obtainUrl(@ApiParam(name = "projectId", value = "工程id", required = true) @RequestParam Long projectId) {
        return R.ok(this.largeScreenService.obtainUrl(projectId));
    }

    @ApiOperation(value = "获取钻孔详情", notes = "获取钻孔详情")
    @GetMapping(value = "/getInfo/{projectId}")
    public R<BizProjectRecordDetailVo> getInfo(@PathVariable("projectId") Long projectId) {
        return R.ok(this.bizProjectRecordService.selectById(projectId));
    }

    @ApiOperation(value = "获取报警记录", notes = "获取报警记录")
    @GetMapping(value = "/obtainAlarmRecord")
    public R<List<AlarmRecordDTO>> obtainAlarmRecord(@ApiParam(name = "alarmType", value = "报警类型") @RequestParam(required = false) String alarmType,
                                                    @ApiParam(name = "startTime", value = "开始时间") @RequestParam(required = false) Long startTime,
                                                    @ApiParam(name = "endTime", value = "结束时间") @RequestParam(required = false) Long endTime) {
        return R.ok(this.largeScreenService.obtainAlarmRecord(alarmType, startTime, endTime));
    }

    @ApiOperation(value = "报警处理", notes = "报警处理")
    @PostMapping(value = "/alarmHandle")
    public R<Object> alarmHandle(@RequestBody HandleDTO handleDTO) {
        return R.ok(this.largeScreenService.alarmHandle(handleDTO));
    }

    @ApiOperation(value = "测试 WebSocket 推送消息（统一格式）", notes = "测试 WebSocket 推送消息")
    @GetMapping(value = "/pushMessageTest")
    public R<String> pushMessageTestUnified() {
        try {

            // 添加工程量报警数据
            List<PlanPushDTO> planList = buildPlanPushList();
            List<AlarmMessage> unifiedList = new ArrayList<>(planList);

            // 添加钻孔间距报警数据
            List<SpaceAlarmPushDTO> alarmList = buildSpaceAlarmList();
            unifiedList.addAll(alarmList);

            // 统一发送
            String message = SendMessageUtils.sendMessage(unifiedList);
            WebSocketServer.sendInfoAll(message);

        } catch (Exception e) {
            log.error("WebSocket消息推送失败", e);
            return R.fail("推送失败：" + e.getMessage());
        }
        return R.ok("统一格式推送成功");
    }


    private List<PlanPushDTO> buildPlanPushList() {
        List<PlanPushDTO> list = new ArrayList<>();
        PlanPushDTO dto = new PlanPushDTO();
        dto.setAlarmType(ConstantsInfo.QUANTITY_ALARM);
        dto.setAlarmId(1L);
        dto.setPlanId(1L);
        dto.setAlarmTime(System.currentTimeMillis());
        dto.setWorkFaceName("11-2101工作面");
        dto.setPlanQuantity(50);
        dto.setActualCompleteQuantity(20);
        dto.setAlarmContent("11-2101工作面：在2025年6月15~2025年6月30日期间，计划完成总钻孔量为10个。规定在50%的时间内，完成总数的50% 。当前实际完成3个，没有达到预期计划，触发报警");
        list.add(dto);
        return list;
    }

    private List<SpaceAlarmPushDTO> buildSpaceAlarmList() {
        List<SpaceAlarmPushDTO> list = new ArrayList<>();

        SpaceAlarmPushDTO dto1 = new SpaceAlarmPushDTO();
        dto1.setAlarmType(ConstantsInfo.DRILL_SPACE_ALARM);
        dto1.setAlarmId(2L);
        dto1.setAlarmTime(System.currentTimeMillis());
        dto1.setCurrentProjectId(1L);
        dto1.setCurrentDrillNum("2");
        dto1.setContrastDrillNum("1");
        dto1.setSpaced(2.0);
        dto1.setActualDistance(2.5);
        dto1.setTunnelName("11-2101回风巷");
        dto1.setWorkFaceName("11-2101工作面");
        dto1.setAlarmContent("11-2101工作面下的11-2101回风巷内，2号钻孔与1号钻孔之间的距离为2.5米，超过当前卸压计划中间距2.0米的要求，发生报警！");
        list.add(dto1);

        SpaceAlarmPushDTO dto2 = new SpaceAlarmPushDTO();
        dto2.setAlarmType(ConstantsInfo.DRILL_SPACE_ALARM);
        dto2.setAlarmId(3L);
        dto2.setAlarmTime(System.currentTimeMillis() + 1000); // 时间略后移
        dto2.setCurrentProjectId(2L);
        dto2.setCurrentDrillNum("4");
        dto2.setContrastDrillNum("3");
        dto2.setSpaced(1.0);
        dto2.setActualDistance(1.5);
        dto2.setTunnelName("11-2101回风巷");
        dto2.setWorkFaceName("11-2101工作面");
        dto2.setAlarmContent("11-2101工作面下的11-2101回风巷内，4号钻孔与3号钻孔之间的距离为1.5米，超过当前卸压计划中间距1.0米的要求，发生报警！");
        list.add(dto2);
        return list;
    }

}