package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;

import com.ruoyi.system.domain.dto.BizMineDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

//import org.springdoc.api.annotations.ParameterObject;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@Api(value = "矿井管理Controller",description = "矿井管理Controller")
//@Tag(description = "矿井管理Controller", name = "矿井管理Controller")
@RestController
@RequestMapping("/basicInfo/mine")
public class BizMineController extends BaseController
{
    @Autowired
    private IBizMineService bizMineService;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询矿井管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/list")
    public R<MPage<BizMine>> list(@ParameterObject BizMineDto dto, @ParameterObject Pagination pagination)
    {
        MPage<BizMine> list = bizMineService.selectBizMineList(dto,pagination);
        return R.ok(list);
    }



    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取矿井管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
    @GetMapping(value = "/{mineId}")
    public AjaxResult getInfo(@PathVariable("mineId") Long mineId)
    {
        return success(bizMineService.selectBizMineByMineId(mineId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:add')")
    @Log(title = "矿井管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated(value = {GroupAdd.class}) BizMineDto dto)
    {
        BizMine entity = new BizMine();
        BeanUtil.copyProperties(dto, entity);
        return toAjax(bizMineService.insertBizMine(entity));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:edit')")
    @Log(title = "矿井管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizMineDto dto)
    {
        BizMine entity = new BizMine();
        BeanUtil.copyProperties(dto, entity);
        return toAjax(bizMineService.updateBizMine(entity));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "矿井管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mineIds}")
    public AjaxResult remove(@PathVariable Long[] mineIds)
    {
        return toAjax(bizMineService.deleteBizMineByMineIds(mineIds));
    }
}
