package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.service.IBizMineService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 矿井管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api("矿井管理Controller")
//@Tag(description = "矿井管理Controller", name = "矿井管理Controller")
@RestController
@RequestMapping("/system/mine")
public class BizMineController extends BaseController
{
    @Autowired
    private IBizMineService bizMineService;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询矿井管理列表")
    @PreAuthorize("@ss.hasPermi('system:mine:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMine bizMine)
    {
        startPage();
        List<BizMine> list = bizMineService.selectBizMineList(bizMine);
        return getDataTable(list);
    }

    /**
     * 导出矿井管理列表
     */
    @ApiOperation("导出矿井管理列表")
    @PreAuthorize("@ss.hasPermi('system:mine:export')")
    @Log(title = "矿井管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizMine bizMine)
    {
        List<BizMine> list = bizMineService.selectBizMineList(bizMine);
        ExcelUtil<BizMine> util = new ExcelUtil<BizMine>(BizMine.class);
        util.exportExcel(response, list, "矿井管理数据");
    }

    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取矿井管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:mine:query')")
    @GetMapping(value = "/{mineId}")
    public AjaxResult getInfo(@PathVariable("mineId") Long mineId)
    {
        return success(bizMineService.selectBizMineByMineId(mineId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增矿井管理")
    @PreAuthorize("@ss.hasPermi('system:mine:add')")
    @Log(title = "矿井管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizMine bizMine)
    {
        return toAjax(bizMineService.insertBizMine(bizMine));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改矿井管理")
    @PreAuthorize("@ss.hasPermi('system:mine:edit')")
    @Log(title = "矿井管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizMine bizMine)
    {
        return toAjax(bizMineService.updateBizMine(bizMine));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除矿井管理")
    @PreAuthorize("@ss.hasPermi('system:mine:remove')")
    @Log(title = "矿井管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{mineIds}")
    public AjaxResult remove(@PathVariable Long[] mineIds)
    {
        return toAjax(bizMineService.deleteBizMineByMineIds(mineIds));
    }
}
