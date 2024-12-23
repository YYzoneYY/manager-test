package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.dto.BizVideoDto;
import com.ruoyi.system.service.IBizVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工程视频Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "project-工程视频")
@RestController
@RequestMapping("/project/video")
public class BizVideoController extends BaseController
{
    @Autowired
    private IBizVideoService bizVideoService;

    /**
     * 查询工程视频列表
     */
    @ApiOperation("查询工程视频列表")
    @PreAuthorize("@ss.hasPermi('project:video:list')")
    @GetMapping("/list")
    public R<List<BizVideo>> list(@ParameterObject BizVideoDto dto)
    {
        QueryWrapper<BizVideo> queryWrapper = new QueryWrapper<BizVideo>();
        queryWrapper.lambda().like(StrUtil.isNotBlank(dto.getFileName()), BizVideo::getFileName, dto.getFileName());
        List<BizVideo> list = bizVideoService.list(queryWrapper);
        return R.ok(list);
    }



    /**
     * 获取工程视频详细信息
     */
    @ApiOperation("获取工程视频详细信息")
    @PreAuthorize("@ss.hasPermi('project:video:query')")
    @GetMapping(value = "/{videoId}")
    public R getInfo(@PathVariable("videoId") Long videoId)
    {
        return R.ok(bizVideoService.getById(videoId));
    }




    /**
     * 新增工程视频
     */
    @ApiOperation("新增工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:add')")
    @Log(title = "工程视频", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BizVideoDto dto) {
        Assert.isTrue(dto.getProjectId() != null, "未绑定工程填报id");
        BizVideo entity = new BizVideo();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizVideoService.save(entity));
    }

    /**
     * 修改工程视频
     */
    @ApiOperation("修改工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:edit')")
    @Log(title = "工程视频", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BizVideoDto dto) {
        BizVideo entity = new BizVideo();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizVideoService.updateById(entity));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "工程视频", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{videoId}")
    public R removeOne(@PathVariable Long videoId) {
        UpdateWrapper<BizVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizVideo::getVideoId, videoId)
                .set(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizVideoService.update(null,updateWrapper));
    }

    /**
     * 删除工程视频
     */
    @ApiOperation("删除工程视频")
    @PreAuthorize("@ss.hasPermi('project:video:remove')")
    @Log(title = "工程视频", businessType = BusinessType.DELETE)
	@DeleteMapping("/{videoIds}")
    public R remove(@PathVariable Long[] videoIds) {
        UpdateWrapper<BizVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizVideo::getVideoId, videoIds)
                .set(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizVideoService.update(null,updateWrapper));
    }
}
