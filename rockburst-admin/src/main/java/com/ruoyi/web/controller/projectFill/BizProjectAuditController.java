package com.ruoyi.web.controller.projectFill;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.mapper.BizProjectAuditMapper;
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
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.service.IBizProjectAuditService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工程填报审核记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@RestController
@RequestMapping("/project/audit")
public class BizProjectAuditController extends BaseController
{
    @Autowired
    private IBizProjectAuditService bizProjectAuditService;
    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    /**
     * 查询工程填报审核记录列表
     */
    @PreAuthorize("@ss.hasPermi('project:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizProjectAudit bizProjectAudit)
    {
        startPage();
        List<BizProjectAudit> list = bizProjectAuditService.list();
        return getDataTable(list);
    }

    /**
     * 导出工程填报审核记录列表
     */
    @PreAuthorize("@ss.hasPermi('project:audit:export')")
    @Log(title = "工程填报审核记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizProjectAudit bizProjectAudit)
    {
        List<BizProjectAudit> list = bizProjectAuditService.list();
        ExcelUtil<BizProjectAudit> util = new ExcelUtil<BizProjectAudit>(BizProjectAudit.class);
        util.exportExcel(response, list, "工程填报审核记录数据");
    }

    /**
     * 获取工程填报审核记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:audit:query')")
    @GetMapping(value = "/{projectAuditId}")
    public AjaxResult getInfo(@PathVariable("projectAuditId") Long projectAuditId)
    {
        return success(bizProjectAuditService.getById(projectAuditId));
    }

    /**
     * 新增工程填报审核记录
     */
    @PreAuthorize("@ss.hasPermi('project:audit:add')")
    @Log(title = "工程填报审核记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizProjectAudit bizProjectAudit)
    {
        return toAjax(bizProjectAuditService.save(bizProjectAudit));
    }

    /**
     * 修改工程填报审核记录
     */
    @PreAuthorize("@ss.hasPermi('project:audit:edit')")
    @Log(title = "工程填报审核记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizProjectAudit bizProjectAudit)
    {
        return toAjax(bizProjectAuditService.updateById(bizProjectAudit));
    }

    /**
     * 删除工程填报审核记录
     */
    @PreAuthorize("@ss.hasPermi('project:audit:remove')")
    @Log(title = "工程填报审核记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{projectAuditIds}")
    public AjaxResult remove(@PathVariable Long[] projectAuditIds)
    {
        return toAjax(bizProjectAuditService.removeById(projectAuditIds));
    }
}
