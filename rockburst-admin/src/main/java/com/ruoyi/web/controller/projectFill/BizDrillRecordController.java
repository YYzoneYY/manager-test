package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.dto.BizDrillRecordDto;
import com.ruoyi.system.service.IBizDrillRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 钻孔参数记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "project-钻孔参数记录")
@RestController
@RequestMapping("/drill/record")
public class BizDrillRecordController extends BaseController
{
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;

    /**
     * 查询钻孔参数记录列表
     */
    @ApiOperation("查询钻孔参数记录列表")
    @PreAuthorize("@ss.hasPermi('drill:record:list')")
    @GetMapping("/list")
    public R<List<BizDrillRecord>> list(@ParameterObject BizDrillRecordDto dto , Pagination pagination)
    {
        QueryWrapper<BizDrillRecord> queryWrapper = new QueryWrapper<BizDrillRecord>();
        queryWrapper.lambda().eq(dto.getStatus() != null , BizDrillRecord::getStatus, dto.getStatus());
        List<BizDrillRecord> list = bizDrillRecordService.list(queryWrapper);
        return R.ok(list);
    }

    @ApiOperation("钻孔参数详情")
//    @PreAuthorize("@ss.hasPermi('drill:record:query')")
    @GetMapping(value = "/{drillRecordId}")
    public R getInfo(@PathVariable("drillRecordId") Long drillRecordId)
    {
        return R.ok(bizDrillRecordService.getById(drillRecordId));
    }


    /**
     * 新增工程视频
     */
    @ApiOperation("新增钻孔参数")
//    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "钻孔参数", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BizDrillRecordDto dto) {
        Assert.isTrue(dto.getProjectId() != null, "未绑定工程填报id");
        BizDrillRecord entity = new BizDrillRecord();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizDrillRecordService.save(entity));
    }

    /**
     * 修改工程视频
     */
    @ApiOperation("修改钻孔参数")
//    @PreAuthorize("@ss.hasPermi('project:video:edit')")
    @Log(title = "工程视频", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BizDrillRecordDto dto) {
        BizDrillRecord entity = new BizDrillRecord();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizDrillRecordService.updateById(entity));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除钻孔参数")
//    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "钻孔参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{drillRecordId}")
    public R removeOne(@PathVariable Long drillRecordId) {
        UpdateWrapper<BizDrillRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizDrillRecord::getDrillRecordId, drillRecordId)
                .set(BizDrillRecord::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDrillRecordService.update(null,updateWrapper));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除钻孔参数")
//    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "钻孔参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{drillRecordIds}")
    public R remove(@PathVariable Long[] drillRecordIds) {
        UpdateWrapper<BizDrillRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizDrillRecord::getDrillRecordId, drillRecordIds)
                .set(BizDrillRecord::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDrillRecordService.update(null,updateWrapper));
    }

}
