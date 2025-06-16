package com.ruoyi.web.controller.business;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.message.WebSocketServer;
import com.ruoyi.quartz.task.impl.RyTaskServiceImpl;
import com.ruoyi.system.domain.dto.largeScreen.*;
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

    @ApiOperation(value = "测试 WebSocket 推送消息", notes = "测试 WebSocket 推送消息")
    @GetMapping(value = "/pushMessageTest")
    public R<String> pushMessageTest(String tag) {
        if (!"1".equals(tag) && !"2".equals(tag)) {
            return R.fail("无效的 tag 参数");
        }
        try {
            if ("1".equals(tag)) {
                PlanPushDTO planPushDTO = new PlanPushDTO();
                planPushDTO.setAlarmType("quantity_alarm");
                planPushDTO.setPlanId(1L);
                planPushDTO.setAlarmTime(System.currentTimeMillis());
                planPushDTO.setPlanStartTime(1748766419000L);
                planPushDTO.setPlanEndTime(1751358419000L);
                planPushDTO.setWorkFaceId(1930163301041434625L);
                planPushDTO.setWorkFaceName("403工作面");
                planPushDTO.setPlanQuantity(50);
                planPushDTO.setActualCompleteQuantity(20);
                sendMessage(planPushDTO);
            } else {
                // 构造多个 SpaceAlarmPushDTO 数据
                List<SpaceAlarmPushDTO> dtoList = new ArrayList<>();
                // 第一条数据
                SpaceAlarmPushDTO dto1 = new SpaceAlarmPushDTO();
                dto1.setAlarmType("drill_space_alarm");
                dto1.setAlarmTime(System.currentTimeMillis());
                dto1.setCurrentProjectId(1L);
                dto1.setCurrentDrillNum("2");
                dto1.setContrastDrillNum("1");
                dto1.setDangerLevelName("中危险区");
                dto1.setSpaced(2.0);
                dto1.setActualDistance(2.5);
                dtoList.add(dto1);
                // 第二条数据
                SpaceAlarmPushDTO dto2 = new SpaceAlarmPushDTO();
                dto2.setAlarmType("drill_space_alarm");
                dto2.setAlarmTime(System.currentTimeMillis() + 1000); // 时间略后移
                dto2.setCurrentProjectId(2L);
                dto2.setCurrentDrillNum("4");
                dto2.setContrastDrillNum("3");
                dto2.setDangerLevelName("高危险区");
                dto2.setSpaced(1.0);
                dto2.setActualDistance(1.5);
                dtoList.add(dto2);
                // 发送整个列表（前端需配合解析为数组）
                sendMessage(dtoList);
            }
        } catch (Exception e) {
            log.error("WebSocket消息推送失败，tag: {}", tag, e);
            return R.fail("推送失败：" + e.getMessage());
        }
        return R.ok("推送成功");
    }

    private void sendMessage(Object dto) throws IOException {
        String message = JSON.toJSONString(dto);
        WebSocketServer.sendInfoAll(message);
    }

}