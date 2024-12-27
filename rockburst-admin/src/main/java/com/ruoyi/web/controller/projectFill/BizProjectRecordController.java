package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.RelatesInfoEntity;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.project.BizProjectPlanDto;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.service.IBizDrillRecordService;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.IBizVideoService;
import com.ruoyi.system.service.PlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private PlanService planService;

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
     * 查询工程填报记录列表
     */
    @ApiOperation("查询施工计划列表")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/planList")
    public R<MPage<PlanEntity>> planlist(@ParameterObject BizProjectPlanDto dto, Pagination pagination)
    {
        long timestamp = System.currentTimeMillis();
        MPJLambdaWrapper<PlanEntity> queryWrapper = new MPJLambdaWrapper<PlanEntity>();
        queryWrapper.select(PlanEntity::getPlanId, PlanEntity::getPlanName)
                .leftJoin(RelatesInfoEntity.class, RelatesInfoEntity::getPlanId, RelatesInfoEntity::getPlanId)
                .eq(StrUtil.isNotEmpty(dto.getDrillType()), PlanEntity::getDrillType, dto.getDrillType())
                .eq(StrUtil.isNotEmpty(dto.getState()), PlanEntity::getState, dto.getState())
                .eq(StrUtil.isNotEmpty(dto.getType()), PlanEntity::getType,dto.getType())
                .eq(StrUtil.isNotEmpty(dto.getType()), RelatesInfoEntity::getType,dto.getType())
                .eq(dto.getLocationId() != null ,RelatesInfoEntity::getPositionId, dto.getLocationId())
                .gt(dto.getIsfinish() != null && dto.getIsfinish() == 0,PlanEntity::getStartTime, timestamp)
                .and(dto.getIsfinish() != null && dto.getIsfinish() == 1,
                        i->i.le(PlanEntity::getStartTime,timestamp).ge(PlanEntity::getEndTime,timestamp));
        IPage<PlanEntity> pages =  planService.page(pagination, queryWrapper);
        return R.ok(new MPage<>(pages));
    }


    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        System.out.println("timestamp = " + timestamp);
    }


    /**
     * 获取工程填报记录详细信息
     */
    @Anonymous
    @ApiOperation("获取工程填报记录详细信息")
//    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/{projectId}")
    public R<BizProjectRecordDetailVo> getInfo(@PathVariable("projectId") Long projectId)
    {
        return R.ok(bizProjectRecordService.selectById(projectId));
    }


    @Anonymous
    @ApiOperation("查询导线点是否已经被填报")
//    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/point")
    public R<Boolean> getPointInfo(@RequestParam("pointId") Long pointId,
                                   @RequestParam("constructType") String constructType)
    {
        QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizProjectRecord::getConstructType,constructType).eq(BizProjectRecord::getTravePointId, pointId);
        long i = bizProjectRecordService.count(queryWrapper);
        return R.ok(i <= 0);
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

    @ApiOperation(value = "提交审核", notes = "提交审核")
    @GetMapping("/submitForReview")
    public R<Object> submitForReview(@ApiParam(name = "projectId", value = "工程id", required = true) @RequestParam Long projectId) {
        return R.ok(bizProjectRecordService.submitForReview(projectId));
    }

    @ApiOperation(value = "撤回", notes = "撤回")
    @GetMapping(value = "/withdraw")
    public R<Object> withdraw(@ApiParam(name = "projectId", value = "计划id", required = true) @RequestParam Long projectId){
        return R.ok(bizProjectRecordService.withdraw(projectId));
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
