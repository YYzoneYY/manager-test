package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.PlanAlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "app-预警记录")
@RestController
@RequestMapping("/app/alarm")
public class AppAlarmController extends BaseController
{
    @Autowired
    private PlanAlarmService planAlarmService;


    /**
     * 新增工程填报记录(无登录验证)
     */
    @ApiOperation("预警信息")
    @Log(title = "预警信息", businessType = BusinessType.INSERT)
    @GetMapping("/getAlarm/{type}")
    public R<?> getalarm(@PathVariable String type)
    {
        return R.ok(planAlarmService.list());
    }


}
