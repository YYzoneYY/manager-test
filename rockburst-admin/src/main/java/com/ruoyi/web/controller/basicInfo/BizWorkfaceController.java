package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.BizWorkfaceDto;
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
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.service.IBizWorkfaceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工作面管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api("工作面管理Controller")
@RestController
@RequestMapping("/system/workface")
public class BizWorkfaceController extends BaseController
{
    @Autowired
    private IBizWorkfaceService bizWorkfaceService;

    /**
     * 查询工作面管理列表
     */
    @ApiOperation("查询工作面管理列表")
    @PreAuthorize("@ss.hasPermi('system:workface:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizWorkfaceDto dto)
    {
        startPage();
        List<BizWorkface> list = bizWorkfaceService.selectBizWorkfaceList(dto);
        return getDataTable(list);
    }

    /**
     * 导出工作面管理列表
     */
    @ApiOperation("导出工作面管理列表")
    @PreAuthorize("@ss.hasPermi('system:workface:export')")
    @Log(title = "工作面管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizWorkfaceDto bizWorkface)
    {
        List<BizWorkface> list = bizWorkfaceService.selectBizWorkfaceList(bizWorkface);
        ExcelUtil<BizWorkface> util = new ExcelUtil<BizWorkface>(BizWorkface.class);
        util.exportExcel(response, list, "工作面管理数据");
    }

    /**
     * 获取工作面管理详细信息
     */
    @ApiOperation("获取工作面管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:workface:query')")
    @GetMapping(value = "/{workfaceId}")
    public AjaxResult getInfo(@PathVariable("workfaceId") Long workfaceId)
    {
        return success(bizWorkfaceService.selectBizWorkfaceByWorkfaceId(workfaceId));
    }

    /**
     * 新增工作面管理
     */
    @ApiOperation("新增工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:add')")
    @Log(title = "工作面管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizWorkface bizWorkface)
    {
        return toAjax(bizWorkfaceService.insertBizWorkface(bizWorkface));
    }

    /**
     * 修改工作面管理
     */
    @ApiOperation("修改工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:edit')")
    @Log(title = "工作面管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizWorkface bizWorkface)
    {
        return toAjax(bizWorkfaceService.updateBizWorkface(bizWorkface));
    }

    /**
     * 删除工作面管理
     */
    @ApiOperation("删除工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:remove')")
    @Log(title = "工作面管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workfaceIds}")
    public AjaxResult remove(@PathVariable Long[] workfaceIds)
    {
        return toAjax(bizWorkfaceService.deleteBizWorkfaceByWorkfaceIds(workfaceIds));
    }
}
