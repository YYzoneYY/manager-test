package com.ruoyi.web.controller.projectFill;

import java.sql.Struct;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.dto.BizVideoDto;
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
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.service.IBizVideoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工程视频Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@RestController
@RequestMapping("/project/video")
public class BizVideoController extends BaseController
{
    @Autowired
    private IBizVideoService bizVideoService;

    /**
     * 查询工程视频列表
     */
    @PreAuthorize("@ss.hasPermi('project:video:list')")
    @GetMapping("/list")
    public R<List<BizVideo>> list(@RequestBody BizVideoDto dto)
    {
        QueryWrapper<BizVideo> queryWrapper = new QueryWrapper<BizVideo>();
        queryWrapper.lambda().like(StrUtil.isNotBlank(dto.getFileName()), BizVideo::getFileName, dto.getFileName());
        List<BizVideo> list = bizVideoService.list(queryWrapper);
        return R.ok(list);
    }



//    /**
//     * 获取工程视频详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('project:video:query')")
//    @GetMapping(value = "/{videoId}")
//    public AjaxResult getInfo(@PathVariable("videoId") Long videoId)
//    {
//        return success(bizVideoService.getById(videoId));
//    }
//
//    /**
//     * 新增工程视频
//     */
//    @PreAuthorize("@ss.hasPermi('project:video:add')")
//    @Log(title = "工程视频", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody BizVideo bizVideo)
//    {
//        return toAjax(bizVideoService.save(bizVideo));
//    }
//
//    /**
//     * 修改工程视频
//     */
//    @PreAuthorize("@ss.hasPermi('project:video:edit')")
//    @Log(title = "工程视频", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody BizVideo bizVideo)
//    {
//        return toAjax(bizVideoService.updateById(bizVideo));
//    }
//
//    /**
//     * 删除工程视频
//     */
//    @PreAuthorize("@ss.hasPermi('project:video:remove')")
//    @Log(title = "工程视频", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{videoIds}")
//    public AjaxResult remove(@PathVariable Long[] videoIds)
//    {
//        return toAjax(bizVideoService.removeById(videoIds));
//    }
}
