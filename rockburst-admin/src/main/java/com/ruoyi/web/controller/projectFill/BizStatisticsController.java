package com.ruoyi.web.controller.projectFill;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.service.IBizProjectRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api("工程填报记录Controller")
@RestController
@RequestMapping("/project/record")
public class BizStatisticsController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;

    /**
     * 查询工程填报记录列表
     */
//    @SaIgnore
    @ApiOperation("防冲工程统计")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/statsProject")
    public Object statsProject(BizProjectRecordDto dto)
    {
        return bizProjectRecordService.statsProject(new BasePermission(), dto);
    }


    @ApiOperation("工程监控")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/monitor")
    public R<MPage<Map<String,Object>>> monitorProject(BizPlanDto dto, Pagination pagination)
    {
        return R.ok(bizProjectRecordService.monitorProject( dto , pagination));
    }


    @ApiOperation("666")
    @GetMapping("get666")
    public void get666(BizProjectRecordDto1 dto , HttpServletResponse response) throws IOException {
        bizProjectRecordService.getReport(dto,response);
    }


    @Anonymous
    @ApiOperation("999")
    @GetMapping("get999")
    public void get999(@RequestParam(required = true) Long mineId, @RequestParam(required = true) String statsDate ,@RequestParam(required = true) Long deptId, HttpServletResponse response) throws IOException {
        bizProjectRecordService.getDayReport(mineId,statsDate,deptId,response);
    }




}
