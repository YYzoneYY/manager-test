package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.service.PlanService;
import com.ruoyi.system.service.PlanAuditService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Api(tags = "计划审核历史")
@RestController
@RequestMapping("/planAuditHistory")
public class PlanAuditHistoryController {

    @Resource
    private PlanAuditService planAuditService;
    @Resource
    private PlanService planService;

    @ApiOperation(value = "查询详情", notes = "查询详情")
    @PreAuthorize("@ss.hasPermi('planAuditHistory:queryById')")
    @GetMapping("/queryById")
    public R<Object> queryById(@ApiParam(name = "panId", value = "计划id", required = true) @RequestParam Long panId) {
        return R.ok(this.planService.queryById(panId));
    }

    @ApiOperation(value = "历史查询", notes = "历史查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PreAuthorize("@ss.hasPermi('planAuditHistory:auditHistoryPage')")
    @PostMapping(value = "/auditHistoryPage")
    public R<Object> auditHistoryPage(@RequestBody SelectPlanDTO selectPlanDTO,
                                      @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                      @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.planAuditService.auditHistoryPage(new BasePermission(), selectPlanDTO, pageNum, pageSize));
    }
}