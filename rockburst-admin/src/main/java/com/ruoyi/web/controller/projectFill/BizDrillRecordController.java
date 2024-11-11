package com.ruoyi.web.controller.projectFill;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
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
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.service.IBizDrillRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 钻孔参数记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api("钻孔参数记录Controller")
@RestController
@RequestMapping("/drill/record")
public class BizDrillRecordController extends BaseController
{
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;

    /**
     * 查询钻孔参数记录列表
     */
    @PreAuthorize("@ss.hasPermi('drill:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDrillRecord bizDrillRecord)
    {
        startPage();
        List<BizDrillRecord> list = bizDrillRecordService.list();
        return getDataTable(list);
    }

    /**
     * 导出钻孔参数记录列表
     */
    @PreAuthorize("@ss.hasPermi('drill:record:export')")
    @Log(title = "钻孔参数记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizDrillRecord bizDrillRecord)
    {
        List<BizDrillRecord> list = bizDrillRecordService.list();
        ExcelUtil<BizDrillRecord> util = new ExcelUtil<BizDrillRecord>(BizDrillRecord.class);
        util.exportExcel(response, list, "钻孔参数记录数据");
    }

    /**
     * 获取钻孔参数记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('drill:record:query')")
    @GetMapping(value = "/{drillRecordId}")
    public AjaxResult getInfo(@PathVariable("drillRecordId") Long drillRecordId)
    {
        return success(bizDrillRecordService.list());
    }

    /**
     * 新增钻孔参数记录
     */
    @PreAuthorize("@ss.hasPermi('drill:record:add')")
    @Log(title = "钻孔参数记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizDrillRecord bizDrillRecord)
    {
        return toAjax(bizDrillRecordService.save(bizDrillRecord));
    }

    /**
     * 修改钻孔参数记录
     */
    @PreAuthorize("@ss.hasPermi('drill:record:edit')")
    @Log(title = "钻孔参数记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizDrillRecord bizDrillRecord)
    {
        return toAjax(bizDrillRecordService.updateById(null));
    }

    /**
     * 删除钻孔参数记录
     */
    @PreAuthorize("@ss.hasPermi('drill:record:remove')")
    @Log(title = "钻孔参数记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{drillRecordIds}")
    public AjaxResult remove(@PathVariable Long[] drillRecordIds)
    {
        return toAjax(bizDrillRecordService.removeById(1));
    }
}
