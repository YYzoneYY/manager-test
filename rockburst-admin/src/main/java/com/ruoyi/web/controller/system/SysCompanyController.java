package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.Entity.SysCompany;
import com.ruoyi.system.service.ISysCompanyService;
import com.ruoyi.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司信息
 * 
 * @author ruoyi
 */
@Api(tags = "basic-公司信息")
@RestController
@RequestMapping("/system/company")
public class SysCompanyController extends BaseController
{
    @Autowired
    private ISysCompanyService sysCompanyService;
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 获取公司列表
     */
//    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @ApiOperation("获取公司列表")
    @GetMapping("/list")
    public AjaxResult list(SysCompany company)
    {
        Long deptId = getDeptId();
        SysDept dept = sysDeptService.selectDeptById(deptId);

        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<SysCompany>();
        queryWrapper.lambda().like(StringUtils.isNotEmpty(company.getCompanyName()),
                        SysCompany::getCompanyName, company.getCompanyName())
                .and(i->i.eq(SysCompany::getCompanyId, dept.getCompanyId())
                        .or()
                        .eq(SysCompany::getParentId, dept.getCompanyId())
                        .or()
                        .apply("FIND_IN_SET({0}, ancestors)", dept.getCompanyId()));
        List<SysCompany> companies = sysCompanyService.list(queryWrapper);
        return success(companies);
    }

    /**
     * 查询公司列表（排除节点）
     */
//    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @ApiOperation("查询公司列表（排除节点）")
    @GetMapping("/list/exclude/{companyId}")
    public AjaxResult excludeChild(@PathVariable(value = "companyId", required = false) Long companyId)
    {
        List<SysCompany> companies = sysCompanyService.list();
        companies.removeIf(d -> d.getCompanyId().intValue() == companyId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), companies + ""));
        return success(companies);
    }

    /**
     * 根据公司编号获取详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:company:query')")
    @ApiOperation("根据公司编号获取详细信息")
    @GetMapping(value = "/{companyId}")
    public AjaxResult getInfo(@PathVariable Long companyId)
    {
        return success(sysCompanyService.getById(companyId));
    }

    /**
     * 新增公司
     */
//    @PreAuthorize("@ss.hasPermi('system:company:add')")
    @ApiOperation("新增公司")
    @Log(title = "新增公司", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysCompany company)
    {
        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysCompany::getCompanyName, company.getCompanyName());
        long count = sysCompanyService.count(queryWrapper);
        if (count > 0)
        {
            return error("新增公司'" + company.getCompanyName() + "'失败，公司名称已存在");
        }
        company.setCreateBy(getUsername());
        return toAjax(sysCompanyService.save(company));
    }

    /**
     * 修改公司
     */
//    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @ApiOperation("修改公司")
    @Log(title = "公司管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysCompany company)
    {
        Long companyId = company.getCompanyId();
        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysCompany::getCompanyName, company).ne(SysCompany::getCompanyId, companyId);
        long count = sysCompanyService.count(queryWrapper);
        if (count >= 1)
        {
            return error("修改公司'" + company.getCompanyName() + "'失败，名称已存在");
        }

        else if (company.getParentId().equals(companyId))
        {
            return error("修改公司'" + company.getCompanyName() + "'失败，上级不能是自己");
        }

        company.setUpdateBy(getUsername());
        return toAjax(sysCompanyService.updateById(company));
    }

    /**
     * 删除公司
     */
//    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @ApiOperation("删除公司")
    @Log(title = "公司管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{companyId}")
    public AjaxResult remove(@PathVariable Long companyId)
    {

//        if (deptService.hasChildByDeptId(deptId))
//        {
//            return warn("存在下级公司,不允许删除");
//        }
//        if (deptService.checkDeptExistUser(deptId))
//        {
//            return warn("公司存在用户,不允许删除");
//        }
//        deptService.checkDeptDataScope(deptId);
        UpdateWrapper<SysCompany> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SysCompany::getCompanyId, companyId)
                .set(SysCompany::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return toAjax(sysCompanyService.update(updateWrapper));
    }
}
