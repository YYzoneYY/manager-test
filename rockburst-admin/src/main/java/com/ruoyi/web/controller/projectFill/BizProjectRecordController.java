package com.ruoyi.web.controller.projectFill;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api("工程填报记录Controller")
@RestController
@RequestMapping("/project/record")
public class BizProjectRecordController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;

    /**
     * 查询工程填报记录列表
     */
    @ApiOperation("查询工程填报记录列表")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/list")
    public Object list(BizProjectRecord bizProjectRecord)
    {
        startPage();
        return getDataTable(bizProjectRecordService.getlist( bizProjectRecord));
    }

    /**
     * 导出工程填报记录列表
     */
    @PreAuthorize("@ss.hasPermi('project:record:export')")
    @Log(title = "工程填报记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizProjectRecord bizProjectRecord)
    {
        QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizProjectRecord::getDeptId, 1);
        List<BizProjectRecord> list = bizProjectRecordService.list(queryWrapper);
        ExcelUtil<BizProjectRecord> util = new ExcelUtil<BizProjectRecord>(BizProjectRecord.class);
        util.exportExcel(response, list, "工程填报记录数据");
    }

    /**
     * 获取工程填报记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId)
    {
        return success(bizProjectRecordService.getById(projectId));
    }

    /**
     * 新增工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:add')")
    @Log(title = "工程填报记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizProjectRecord bizProjectRecord)
    {
        return toAjax(bizProjectRecordService.save(bizProjectRecord));
    }

    /**
     * 修改工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:edit')")
    @Log(title = "工程填报记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizProjectRecord bizProjectRecord)
    {
        return toAjax(bizProjectRecordService.updateById(bizProjectRecord));
    }

    /**
     * 删除工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:remove')")
    @Log(title = "工程填报记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        return toAjax(bizProjectRecordService.removeById(projectIds));
    }
}
