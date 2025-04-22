package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.SysAlarmRecord;
import com.ruoyi.system.service.IBizVideoService;
import com.ruoyi.system.service.ISysAlarmRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "预警记录")
@RestController
@RequestMapping("/sys/msg")
public class SysAlarmRecordController extends BaseController
{
    @Autowired
    private ISysAlarmRecordService sysAlarmRecordService;
    @Autowired
    private IBizVideoService bizVideoService;


    @ApiOperation("预警记录查询")
    @GetMapping("/pageAlarm")
    public R<MPage<SysAlarmRecord>> pageAlarm(@ParameterObject SysAlarmRecord record , Pagination pagination)
    {
        return R.ok(sysAlarmRecordService.pageList(new BasePermission(),record ,pagination));
    }


    @ApiOperation("预警点击")
    @PostMapping("/click")
    public R click(@ParameterObject SysAlarmRecord record)
    {
        record.setStatus(1);
        sysAlarmRecordService.updateById(record);
        return R.ok();
    }


}
