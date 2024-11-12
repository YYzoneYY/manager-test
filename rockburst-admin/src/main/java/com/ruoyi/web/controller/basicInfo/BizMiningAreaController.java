package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 采区管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api("采区管理Controller")
@RestController
@RequestMapping("/system/area")
public class BizMiningAreaController extends BaseController
{
    @Autowired
    private IBizMiningAreaService bizMiningAreaService;

    /**
     * 查询采区管理列表
     */
    @ApiOperation("查询采区管理列表")
    @PreAuthorize("@ss.hasPermi('system:area:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMiningArea bizMiningArea)
    {
        startPage();
        List<BizMiningArea> list = bizMiningAreaService.selectBizMiningAreaList(bizMiningArea);
        return getDataTable(list);
    }

    /**
     * 导出采区管理列表
     */
    @ApiOperation("导出采区管理列表")
    @PreAuthorize("@ss.hasPermi('system:area:export')")
    @Log(title = "采区管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizMiningArea bizMiningArea)
    {
        List<BizMiningArea> list = bizMiningAreaService.selectBizMiningAreaList(bizMiningArea);
        ExcelUtil<BizMiningArea> util = new ExcelUtil<BizMiningArea>(BizMiningArea.class);
        util.exportExcel(response, list, "采区管理数据");
    }

    /**
     * 获取采区管理详细信息
     */
    @ApiOperation("获取采区管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:area:query')")
    @GetMapping(value = "/{miningAreaId}")
    public AjaxResult getInfo(@PathVariable("miningAreaId") Long miningAreaId)
    {
        return success(bizMiningAreaService.selectBizMiningAreaByMiningAreaId(miningAreaId));
    }

    /**
     * 新增采区管理
     */
    @ApiOperation("新增采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:add')")
    @Log(title = "采区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizMiningArea bizMiningArea)
    {
        return toAjax(bizMiningAreaService.insertBizMiningArea(bizMiningArea));
    }

    /**
     * 修改采区管理
     */
    @ApiOperation("修改采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:edit')")
    @Log(title = "采区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizMiningArea bizMiningArea)
    {
        return toAjax(bizMiningAreaService.updateBizMiningArea(bizMiningArea));
    }

    /**
     * 删除采区管理
     */
    @ApiOperation("删除采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:remove')")
    @Log(title = "采区管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{miningAreaIds}")
    public AjaxResult remove(@PathVariable Long[] miningAreaIds)
    {
        return toAjax(bizMiningAreaService.deleteBizMiningAreaByMiningAreaIds(miningAreaIds));
    }
}
