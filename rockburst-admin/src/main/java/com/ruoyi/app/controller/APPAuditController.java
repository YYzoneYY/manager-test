package com.ruoyi.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.domain.dto.AppAuditDetailDTO;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.project.BizProjectAPPAuditDto;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/1/13
 * @description:
 */

@Api(tags = "填报审核(App)")
@RestController
@RequestMapping("/auditForApp")
public class APPAuditController {

    @Resource
    private AppAuditService appAuditService;

    @Resource
    private TeamAuditService teamAuditService;

    @Resource
    private DepartmentAuditService departmentAuditService;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;
    @Resource
    private IBizProjectAuditService bizProjectAuditService;


    @ApiOperation(value = "点击审核按钮(区队)", notes = "点击审核按钮(区队)")
    @GetMapping("/clickAuditForTeam")
    public R auditFormTeam(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
        return R.ok(bizProjectAuditService.audit(projectId));
    }


    @ApiOperation(value = "审核(区队)", notes = "审核(区队)")
    @PostMapping(value = "/addAuditFormTeam")
    public R<Object> addTeamAudit(@RequestBody BizProjectAudit teamAuditDTO) {
        bizProjectAuditService.addAudittEAM(teamAuditDTO);
        return R.ok();
    }

    @ApiOperation(value = "审核(科室)", notes = "审核(科室)")
    @PostMapping(value = "/addAuditFormDepart")
    public R<Object> addDepartAudit(@RequestBody BizProjectAudit teamAuditDTO) {
        return R.ok(bizProjectAuditService.addAuditDeart(teamAuditDTO));
    }


    @ApiOperation(value = "审核分页查询", notes = "审核分页查询")
    @GetMapping(value = "/pendingApprovalForTeam")
    public R<Object> teamAuditByPage(@ParameterObject BizProjectAPPAuditDto dto, Pagination pagination) {
        BizProjectRecordDto dd = new BizProjectRecordDto();
        BeanUtil.copyProperties(dto, dd);
        return R.ok(bizProjectRecordService.pageAudit(new BasePermission(),dd , dto.getStatuss(), pagination));
    }
//    @ApiOperation(value = "审核(区队)", notes = "审核(区队)")
//    @PostMapping(value = "/addAuditFormTeam")
//    public R<Object> addTeamAudit(@RequestBody @Validated(ParameterValidationOther.class) TeamAuditDTO teamAuditDTO) {
//        return R.ok(this.teamAuditService.addTeamAudit(teamAuditDTO));
//    }

//    @ApiOperation(value = "待审核分页查询(区队)", notes = "待审核分页查询(区队)")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
//            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
//    })
//    @GetMapping(value = "/pendingApprovalForTeam")
//    public R<Object> teamAuditByPage(@ApiParam(name = "fillingType", value = "填报类型") @RequestParam(required = false) String fillingType,
//                               @ApiParam(name = "constructionUnitId", value = "施工单位id")  @RequestParam(required = false) Long constructionUnitId,
//                                     Pagination pagination) {
//        return R.ok(bizProjectRecordService.getlistAdudit(new BasePermission(),fillingType,constructionUnitId,new Integer[]{1,2}, pagination));
//    }
//
//    @ApiOperation(value = "已审核分页查询(区队)", notes = "已审核分页查询(区队)")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
//            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
//    })
//    @GetMapping(value = "/approvedForTeam")
//    public R<Object> teamApprovedByPage(@ApiParam(name = "userId", value = "用户id", required = true) @RequestParam Long userId,
//                               @ApiParam(name = "fillingType", value = "填报类型") @RequestParam(required = false) String fillingType,
//                               @ApiParam(name = "constructionUnitId", value = "施工单位id")  @RequestParam(required = false) Long constructionUnitId,
//                                        Pagination pagination) {
//        SelectDTO selectDTO = new SelectDTO(fillingType, constructionUnitId);
//        return R.ok(this.appAuditService.teamApprovedByPage(selectDTO, userId, pagination));
//    }
//
//    @ApiOperation(value = "点击审核按钮(科室)", notes = "点击审核按钮(科室)")
//    @GetMapping(value = "/clickAuditFormDepart")
//    public R<DepartmentAuditDTO> auditFormDepart(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
//        return R.ok(this.departmentAuditService.clickAudit(projectId));
//    }
//
//    @ApiOperation(value = "审核(科室)", notes = "审核(科室)")
//    @PostMapping(value = "/addAuditFormDepart")
//    public R<Object> addDepartAudit(@RequestBody @Validated(ParameterValidationOther.class) DepartAuditDTO departAuditDTO) {
//        return R.ok(this.departmentAuditService.departmentAudit(departAuditDTO));
//    }
//
//    @ApiOperation(value = "待审核分页查询(科室)", notes = "待审核分页查询(科室)")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
//            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
//    })
//    @GetMapping(value = "/pendingApprovalForDepart")
//    public R<Object> departAuditByPage(@ApiParam(name = "fillingType", value = "填报类型") @RequestParam(required = false) String fillingType,
//                                     @ApiParam(name = "constructionUnitId", value = "施工单位id")  @RequestParam(required = false) Long constructionUnitId,
//                                     @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
//                                     @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
//        SelectDTO selectDTO = new SelectDTO(fillingType, constructionUnitId);
//        return R.ok(this.appAuditService.departAuditByPage(selectDTO, pageNum, pageSize));
//    }
//
//    @ApiOperation(value = "已审核分页查询(科室)", notes = "已审核分页查询(科室)")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
//            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
//    })
//    @GetMapping(value = "/approvedForDepart")
//    public R<Object> departApprovedByPage(@ApiParam(name = "userId", value = "用户id", required = true) @RequestParam Long userId,
//                                        @ApiParam(name = "fillingType", value = "填报类型") @RequestParam(required = false) String fillingType,
//                                        @ApiParam(name = "constructionUnitId", value = "施工单位id")  @RequestParam(required = false) Long constructionUnitId,
//                                        @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
//                                        @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
//        SelectDTO selectDTO = new SelectDTO(fillingType, constructionUnitId);
//        return R.ok(this.appAuditService.departApprovedByPage(selectDTO, userId, pageNum, pageSize));
//    }

    @ApiOperation(value = "详情", notes = "详情")
    @GetMapping(value = "/detail")
    public R<AppAuditDetailDTO> detail(@ApiParam(name = "projectId", value = "工程填报id", required = true) @RequestParam Long projectId,
                                      @ApiParam(name = "tag", value = "审批类型标识(1-区队审核、2-科室审核)", required = true) @RequestParam String tag) {
        return R.ok(this.appAuditService.detail(projectId, tag));
    }

}