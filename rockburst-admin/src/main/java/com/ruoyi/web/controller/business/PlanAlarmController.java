package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.Entity.PlanAlarm;
import com.ruoyi.system.service.PlanAlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Api(tags = "计划预警")
@RestController
@RequestMapping("/planAlarm")
public class PlanAlarmController {

    @Resource
    private PlanAlarmService planAlarmService;


    @ApiOperation("查询矿井管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/list")
    public R<MPage<PlanAlarm>> list( @ParameterObject Pagination pagination)
    {
        MPage<PlanAlarm> list = planAlarmService.selectPageList(null,pagination);
        return R.ok(list);
    }

}