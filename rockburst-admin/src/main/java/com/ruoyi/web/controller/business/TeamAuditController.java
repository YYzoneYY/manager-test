package com.ruoyi.web.controller.business;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.dto.TeamAuditDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.TeamAuditService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Api(tags = "区队审核")
@RestController
@RequestMapping("/teamAudit")
public class TeamAuditController {

    @Resource
    private TeamAuditService teamAuditService;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;

    @ApiOperation(value = "点击审核按钮", notes = "点击审核按钮")
    @PreAuthorize("@ss.hasPermi('teamAudit:clickAudit')")
    @GetMapping("/clickAudit")
    public R<BizProjectRecordDetailVo> audit(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
        return R.ok(this.teamAuditService.audit(projectId));
    }

    @ApiOperation(value = "审核", notes = "审核")
    @PreAuthorize("@ss.hasPermi('teamAudit:addAudit')")
    @PostMapping(value = "/addAudit")
    public R<Object> addTeamAudit(@RequestBody @Validated(ParameterValidationOther.class) TeamAuditDTO teamAuditDTO) {
        return R.ok(this.teamAuditService.addTeamAudit(teamAuditDTO));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PreAuthorize("@ss.hasPermi('teamAudit:queryPage')")
    @PostMapping(value = "/queryPage")
    public R<Object> queryPage(@RequestBody SelectProjectDTO selectProjectDTO,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.teamAuditService.queryByPage(new BasePermission(), selectProjectDTO, pageNum, pageSize));
    }

    /**
     * 修改工程填报记录
     */
    @ApiOperation("工程填报信息修改")
    @PreAuthorize("@ss.hasPermi('teamAudit:projectInfoEdit')")
    @Log(title = "工程填报记录", businessType = BusinessType.UPDATE)
    @PutMapping("/projectInfoEdit")
    public R<?> edit(@RequestBody BizProjectRecordAddDto bizProjectRecord)
    {
        return R.ok(bizProjectRecordService.updateRecord(bizProjectRecord));
    }
}