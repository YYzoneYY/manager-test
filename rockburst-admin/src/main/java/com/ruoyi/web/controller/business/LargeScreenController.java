package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.largeScreen.*;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.LargeScreenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
}