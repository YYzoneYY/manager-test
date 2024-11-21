package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
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

import java.util.List;

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
    public R<MPage<BizProjectRecordListVo>> list(@ParameterObject BizProjectRecordDto dto, Pagination pagination)
    {
        MPage<BizProjectRecordListVo> llis =  bizProjectRecordService.getlist(new BasePermission(), dto , pagination);
        return R.ok(llis);
    }


    @ApiOperation("防冲工程查询")
//    @PreAuthorize("@ss.hasPermi('project:record:auditList')")
    @GetMapping("/selectproList")
    public R<MPage<BizProjectRecordListVo>> selectproList(@ParameterObject BizProjectRecordDto dto, Pagination pagination)
    {
        return R.ok(bizProjectRecordService.selectproList(new BasePermission(), dto,pagination));
    }


    /**
     * 获取工程填报记录详细信息
     */
//    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/{projectId}")
    public R<BizProjectRecordListVo> getInfo(@PathVariable("projectId") Long projectId)
    {
        BizProjectRecord vo = bizProjectRecordService.getByIdDeep(projectId);
        BizProjectRecordListVo vo1 = new BizProjectRecordListVo();
        BeanUtil.copyProperties(vo, vo1);
        return R.ok(vo1);
    }

    /**
     * 新增工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:add')")
    @Log(title = "工程填报记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<?> add(@RequestBody BizProjectRecordAddDto dto)
    {
        return R.ok(bizProjectRecordService.saveRecord(dto));
    }

    /**
     * 修改工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:edit')")
    @Log(title = "工程填报记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<?> edit(@RequestBody BizProjectRecordAddDto bizProjectRecord)
    {
        return R.ok(bizProjectRecordService.updateRecord(bizProjectRecord));
    }

    /**
     * 删除工程填报记录
     */
    @PreAuthorize("@ss.hasPermi('project:record:remove')")
    @Log(title = "工程填报记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{projectIds}")
    public R<?> remove(@PathVariable Long[] projectIds)
    {
        return R.ok(bizProjectRecordService.removeById(projectIds));
    }


    @PreAuthorize("@ss.hasPermi('project:record:edit')")
    @Log(title = "修改阅读状态", businessType = BusinessType.UPDATE)
    @PutMapping("/read/{projecctId}")
    public R<?> read(@PathVariable("projecctId") Long projecctId)
    {
        BizProjectRecord bizProjectRecord = new BizProjectRecord();
        bizProjectRecord.setProjectId(projecctId).setIsRead(1);
        return R.ok(bizProjectRecordService.updateById(bizProjectRecord));
    }

//    @PreAuthorize("@ss.hasPermi('project:record:edit')")
//    @Log(title = "区队审核工程填报记录", businessType = BusinessType.UPDATE)
//    @PutMapping("/firstAudit")
//    public AjaxResult firstAudit(@RequestBody BizProjectRecordDto dto)
//    {
//        return toAjax(bizProjectRecordService.firstAudit(dto));
//    }
//
//    @PreAuthorize("@ss.hasPermi('project:record:edit')")
//    @Log(title = "科室审核工程填报记录", businessType = BusinessType.UPDATE)
//    @PutMapping("/secondAudit")
//    public AjaxResult secondAudit(@RequestBody BizProjectRecordDto dto)
//    {
//        return toAjax(bizProjectRecordService.secondAudit(dto));
//    }


}
