package com.ruoyi.web.controller.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.service.IBizProjectAuditService;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Api(tags = "区队审核")
@RestController
@RequestMapping("/audit")
public class TeamAuditController {

//    @Resource
//    private TeamAuditService teamAuditService;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;

    @Resource
    private IBizProjectAuditService bizProjectAuditService;

    @Resource
    private ISysUserService sysUserService;

    @ApiOperation(value = "点击审核按钮", notes = "点击审核按钮")
    @PreAuthorize("@ss.hasPermi('teamAudit:clickAudit')")
    @GetMapping("/clickAudit")
    public R audit(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
        return R.ok(bizProjectAuditService.audit(projectId));
    }

//    @ApiOperation(value = "审核", notes = "审核")
//    @PreAuthorize("@ss.hasPermi('teamAudit:addAudit')")
//    @PostMapping(value = "/addAudit")
//    public R<Object> addTeamAudit(@RequestBody @Validated(ParameterValidationOther.class) TeamAuditDTO teamAuditDTO) {
//        return R.ok(this.teamAuditService.addTeamAudit(teamAuditDTO));
//    }

    @ApiOperation(value = "审核区队", notes = "审核区队")
    @PreAuthorize("@ss.hasPermi('teamAudit:addAudit')")
    @PostMapping(value = "/addAuditTeam")
    public R<Object> addAuditTeam(@RequestBody BizProjectAudit teamAuditDTO) {
        bizProjectAuditService.addAudittEAM(teamAuditDTO);
        return R.ok();
    }


    @ApiOperation(value = "审核科室", notes = "审核科室")
    @PreAuthorize("@ss.hasPermi('teamAudit:addAudit')")
    @PostMapping(value = "/addAuditDeart")
    public R<Object> addAuditDeart(@RequestBody BizProjectAudit teamAuditDTO) {
        bizProjectAuditService.addAuditDeart(teamAuditDTO);
        return R.ok();
    }


//    @ApiOperation(value = "分页查询", notes = "分页查询")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
//            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
//    })
//    @PreAuthorize("@ss.hasPermi('teamAudit:queryPage')")
//    @PostMapping(value = "/queryPage")
//    public R<Object> queryPage(@RequestBody SelectProjectDTO selectProjectDTO,
//                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
//                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
//        return R.ok(this.teamAuditService.queryByPage(new BasePermission(), selectProjectDTO, pageNum, pageSize));
//    }

    @ApiOperation(value = "区队分页查询", notes = "区队分页查询")
    @PreAuthorize("@ss.hasPermi('teamAudit:queryPage')")
    @GetMapping(value = "/queryPageTeam")
    public R<Object> queryPageTeam( @ParameterObject BizProjectRecordDto dto, Pagination pagination) {
        return R.ok(bizProjectRecordService.pageAudit(new BasePermission(),dto , new Integer[]{1,2}, pagination));
    }

    @ApiOperation(value = "科室分页查询", notes = "科室分页查询")
    @PreAuthorize("@ss.hasPermi('teamAudit:queryPage')")
    @GetMapping(value = "/queryPageDeart")
    public R<Object> queryPageDeart( @ParameterObject BizProjectRecordDto dto, Pagination pagination) {
        return R.ok(bizProjectRecordService.pageAudit(new BasePermission(),dto , new Integer[]{3,5}, pagination));
    }

    @ApiOperation(value = "历史分页查询", notes = "历史分页查询")
    @PreAuthorize("@ss.hasPermi('teamAudit:queryPage')")
    @GetMapping(value = "/queryPageHistory")
    public R<Object> queryPageHistory( @ParameterObject BizProjectRecordDto dto, Pagination pagination) {
        return R.ok(bizProjectRecordService.pageAudit(new BasePermission(),dto , new Integer[]{4,6,7}, pagination));
    }

    @ApiOperation("详细信息")
    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/{projectId}")
    public R<BizProjectRecordDetailVo> getInfo(@PathVariable("projectId") Long projectId)
    {
        BizProjectRecordDetailVo vo= bizProjectRecordService.selectById(projectId);
        QueryWrapper<BizProjectAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizProjectAudit::getProjectId,projectId)
                            .orderByDesc(BizProjectAudit::getNo)
                                .last("LIMIT 1");
        List<BizProjectAudit> vos =  bizProjectAuditService.list(queryWrapper);
        if(vos != null && vos.size()>0){

            vo.setAuditLastName(sysUserService.selectUserByUserName(vos.get(0).getCreateBy()).getNickName());

        }
        return R.ok(vo);
    }

//    /**
//     * 修改工程填报记录
//     */
//    @ApiOperation("工程填报信息修改")
//    @PreAuthorize("@ss.hasPermi('teamAudit:projectInfoEdit')")
//    @Log(title = "工程填报记录", businessType = BusinessType.UPDATE)
//    @PutMapping("/projectInfoEdit")
//    public R<?> edit(@RequestBody BizProjectRecordAddDto bizProjectRecord)
//    {
//        return R.ok(bizProjectRecordService.updateRecord(bizProjectRecord));
//    }
}