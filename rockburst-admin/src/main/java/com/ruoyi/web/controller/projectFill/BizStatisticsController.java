package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.dto.project.BizCardVDto;
import com.ruoyi.system.domain.dto.project.BizWashProofDto;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordPaibanVo;
import com.ruoyi.system.service.IBizDrillRecordService;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.IBizVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "防冲工程")
@RestController
@RequestMapping("/project/record")
public class BizStatisticsController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;
    @Autowired
    private IBizVideoService bizVideoService;
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;


    @ApiOperation("防冲工程查询")
//    @PreAuthorize("@ss.hasPermi('project:record:auditList')")
    @GetMapping("/selectproList")
    public R<MPage<BizProjectRecordListVo>> selectproList(@ParameterObject BizWashProofDto dto, Pagination pagination)
    {
        return R.ok(bizProjectRecordService.selectproList(new BasePermission(), dto,pagination));
    }


    @ApiOperation("获取防冲工程查询详细信息")
//    @PreAuthorize("@ss.hasPermi('project:record:query')")
    @GetMapping(value = "/fc/{projectId}")
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
     * 查询工程填报记录列表
     */
//    @SaIgnore
    @ApiOperation("防冲工程统计")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/statsProject")
    public Object statsProject(@ParameterObject BizWashProofDto dto)
    {
        return bizProjectRecordService.statsProject(new BasePermission(), dto);
    }

//    @Anonymous
    @ApiOperation("牌板查询")
//    @PreAuthorize("@ss.hasPermi('project:record:auditList')")
    @GetMapping("/selectPaiList")
    public R<MPage<BizProjectRecordPaibanVo>> selectPaiList(@ParameterObject BizCardVDto dto, Pagination pagination)
    {
        return R.ok(bizProjectRecordService.selectPaiList(new BasePermission(), dto,pagination));
    }

    @Anonymous
    @ApiOperation("工程监控")
//    @PreAuthorize("@ss.hasPermi('project:record:list')")
    @GetMapping("/monitor")
    public R<MPage<Map<String,Object>>> monitorProject(BizPlanDto dto, Pagination pagination)
    {
        return R.ok(bizProjectRecordService.monitorProject( dto , pagination));
    }

    @Anonymous
    @ApiOperation("煤粉量日报表")
    @GetMapping("get666")
    public void get666(BizProjectRecordDto1 dto , HttpServletResponse response) throws IOException {
        bizProjectRecordService.get444(dto,response);
    }


    @Anonymous
    @ApiOperation("999")
    @GetMapping("get999")
    public void get999(@RequestParam(required = true) Long mineId, @RequestParam(required = true) String statsDate ,@RequestParam(required = true) Long deptId, HttpServletResponse response) throws IOException {
        bizProjectRecordService.getDayReport(mineId,statsDate,deptId,response);
    }


    @Anonymous
    @ApiOperation("444")
    @GetMapping("444")
//    public void get444(@RequestParam(required = true) Long mineId, @RequestParam(required = true) String statsDate ,@RequestParam(required = true) Long deptId, HttpServletResponse response) throws IOException {
    public void get444(HttpServletResponse response) throws IOException {
//        bizProjectRecordService.get444(response);
        bizProjectRecordService.sss555(response);
    }

}
