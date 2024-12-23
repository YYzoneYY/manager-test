package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.service.IBizDrillRecordService;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.IBizVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "project-工程填报记录")
@RestController
@RequestMapping("/project/record")
public class BizProjectRecordController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;

    @Autowired
    private IBizDrillRecordService bizDrillRecordService;
    @Autowired
    private IBizVideoService bizVideoService;

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




    /**
     * 获取工程填报记录详细信息
     */
    @ApiOperation("获取工程填报记录详细信息")
//    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/{projectId}")
    public R<BizProjectRecordDetailVo> getInfo(@PathVariable("projectId") Long projectId)
    {
        BizProjectRecord record = bizProjectRecordService.getByIdDeep(projectId);
        BizProjectRecordDetailVo vo = new BizProjectRecordDetailVo();
        BeanUtil.copyProperties(record, vo);
        QueryWrapper<BizDrillRecord> drillRecordQueryWrapper = new QueryWrapper<BizDrillRecord>();
        drillRecordQueryWrapper.lambda().eq(BizDrillRecord::getProjectId, record.getProjectId()).eq(BizDrillRecord::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<BizDrillRecord> drillRecordList =  bizDrillRecordService.listDeep(drillRecordQueryWrapper);

        QueryWrapper<BizVideo> videoQueryWrapper = new QueryWrapper<BizVideo>();
        videoQueryWrapper.lambda().eq(BizVideo::getProjectId, record.getProjectId()).eq(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<BizVideo> videos =  bizVideoService.listDeep(videoQueryWrapper);
        vo.setVideoList(videos).setDrillRecordList(drillRecordList);
        return R.ok(vo);
    }

    /**
     * 新增工程填报记录
     */
    @ApiOperation("新增工程填报记录")
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
    @ApiOperation("修改工程填报记录")
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
    @ApiOperation("删除工程填报记录")
    @PreAuthorize("@ss.hasPermi('project:record:remove')")
    @Log(title = "工程填报记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{projectId}")
    public R<?> removeById(@PathVariable Long projectId)
    {
        return R.ok(bizProjectRecordService.removeByProId(projectId));
    }


    /**
     * 删除工程填报记录
     */
    @ApiOperation("删除工程填报记录")
    @PreAuthorize("@ss.hasPermi('project:record:remove')")
    @Log(title = "工程填报记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{projectIds}")
    public R<?> remove(@PathVariable Long[] projectIds)
    {

        return R.ok(bizProjectRecordService.removeByProIds(projectIds));
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
