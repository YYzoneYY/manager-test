package com.ruoyi.web.controller.system;

import cn.hutool.core.util.StrUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        if(getUsername().equals("admin")){
            QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<SysCompany>();
            queryWrapper.lambda().like(StringUtils.isNotEmpty(company.getCompanyName()),
                            SysCompany::getCompanyName, company.getCompanyName())
                    .eq(company.getStatus() != null , SysCompany::getStatus, company.getStatus());
            List<SysCompany> companies = sysCompanyService.list(queryWrapper);
            return success(companies);
        }
        Long deptId = getDeptId();
        SysDept dept = sysDeptService.selectDeptById(deptId);

        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<SysCompany>();
        queryWrapper.lambda().like(StringUtils.isNotEmpty(company.getCompanyName()),
                        SysCompany::getCompanyName, company.getCompanyName())
                .eq(company.getStatus() != null , SysCompany::getStatus, company.getStatus())
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

        if(StrUtil.isNotEmpty(company.getMineIds())){
            String mineIds = company.getMineIds();
            List<Long> mineIdList = Arrays.stream(mineIds.split(","))
                    .map(String::trim)   // 去掉空格
                    .filter(s -> !s.isEmpty()) // 过滤空字符串
                    .map(Long::valueOf)  // 转 Long
                    .collect(Collectors.toList());
            Long counted = 0l;
            for(Long mineId : mineIdList){
                queryWrapper.clear();
                queryWrapper.apply("FIND_IN_SET({0}, mine_ids)", mineId);
                counted = counted + sysCompanyService.count(queryWrapper);
            }
            if(counted > 0l){
                return error("新增公司'"  + "'失败,矿井已经再其他公司下");
            }

        }


        //先查祖籍
        List<Long> ancestorIds = getAncestors(company.getParentId());
        company.setCreateBy(getUsername());
        company.setAncestors(StringUtils.join(ancestorIds, ","));

        return toAjax(sysCompanyService.save(company));
    }



    public List<Long> getAncestors(Long parentId) {
        List<Long> ancestors = new ArrayList<>();
        findAncestors(parentId, ancestors);
        // 最后补一个 0
        if (ancestors.isEmpty() || !ancestors.get(ancestors.size() - 1).equals(0L)) {
            ancestors.add(0L);
        }
        Collections.reverse(ancestors);
        return ancestors;
    }

    private void findAncestors(Long parentId, List<Long> ancestors) {
        if (parentId == null) {
            return;
        }
        SysCompany company = sysCompanyService.getById(parentId);
        if (company != null) {
            ancestors.add(parentId); // 记录当前节点
            findAncestors(company.getParentId(), ancestors); // 递归往上找
        } else {
            // 查不到公司，但仍然要补 0
            ancestors.add(0L);
        }
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
        queryWrapper.lambda().eq(SysCompany::getCompanyName, company.getCompanyName()).ne(SysCompany::getCompanyId, companyId);
        long count = sysCompanyService.count(queryWrapper);
        if (count >= 1)
        {
            return error("修改公司'" + company.getCompanyName() + "'失败，名称已存在");
        }

        else if (company.getParentId().equals(companyId))
        {
            return error("修改公司'" + company.getCompanyName() + "'失败，上级不能是自己");
        }

        if(StrUtil.isNotEmpty(company.getMineIds())){
            String mineIds = company.getMineIds();
            List<Long> mineIdList = Arrays.stream(mineIds.split(","))
                    .map(String::trim)   // 去掉空格
                    .filter(s -> !s.isEmpty()) // 过滤空字符串
                    .map(Long::valueOf)  // 转 Long
                    .collect(Collectors.toList());
            Long counted = 0l;
            for(Long mineId : mineIdList){
                queryWrapper.clear();
                queryWrapper.lambda().apply("FIND_IN_SET({0}, mine_ids)", mineId)
                        .ne(SysCompany::getCompanyId,companyId);
                counted = counted + sysCompanyService.count(queryWrapper);
            }
            if(counted > 0l){
                return error("新增公司'"  + "'失败,矿井已经再其他公司下");
            }

        }
        List<Long> ancestorIds = getAncestors(company.getParentId());
        company.setAncestors(StringUtils.join(ancestorIds, ","));
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

        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysCompany::getParentId, companyId);
        long count = sysCompanyService.count(queryWrapper);
        if (count > 0l)
        {
            return warn("存在下级公司,不允许删除");
        }

        SysDept dept = new SysDept();
        dept.setParentId(companyId);
        List<SysDept> depts = sysDeptService.selectDeptList(dept);
        if (depts != null && depts.size() > 0)
        {
            return warn("公司存在用户,不允许删除");
        }
        UpdateWrapper<SysCompany> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SysCompany::getCompanyId, companyId)
                .set(SysCompany::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return toAjax(sysCompanyService.update(updateWrapper));
    }
}
