package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.DepartAuditHistoryDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.service.DepartmentAuditService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Api(tags = "历史查询")
@RestController
@RequestMapping("/projectAuditHistory")
public class ProjectAuditHistoryController {

    @Resource
    private DepartmentAuditService departmentAuditService;

    @ApiOperation(value = "历史查询", notes = "历史查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PreAuthorize("@ss.hasPermi('projectAuditHistory:auditHistoryPage')")
    @PostMapping(value = "/auditHistoryPage")
    public R<Object> auditHistoryPage(@RequestBody SelectProjectDTO selectProjectDTO,
                                      @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                      @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.departmentAuditService.auditHistoryPage(new BasePermission(), selectProjectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "详情", notes = "详情")
    @PreAuthorize("@ss.hasPermi('projectAuditHistory:detail')")
    @GetMapping(value = "/detail")
    public R<DepartAuditHistoryDTO> detail(@ApiParam(name = "projectId", value = "工程填报id", required = true) @RequestParam Long projectId) {
        return R.ok(this.departmentAuditService.detail(projectId));
    }

}