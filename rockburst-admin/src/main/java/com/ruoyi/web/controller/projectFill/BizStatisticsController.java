package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.dto.project.BizCardVDto;
import com.ruoyi.system.domain.dto.project.BizWashProofDto;
import com.ruoyi.system.domain.utils.CrumbWeight;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordPaibanVo;
import com.ruoyi.system.domain.vo.BizPulverizedCoalDaily;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
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
import java.util.*;

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
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;


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

//    @Anonymous
    @ApiOperation("煤粉量查询")
    @GetMapping("getPulverizedCoalDaily")
    public R<MPage<BizPulverizedCoalDaily>> getPulverizedCoalDaily(@RequestParam(name = "工作面id", required = false) Long workfaceId,
                                       @RequestParam(name = "巷道id", required = false) Long tunnelId,
                                       @RequestParam(name = "施工类型", required = false) String constructType,
                                       @RequestParam(name = "开始日期", required = false) String startDate,
                                       @RequestParam(name = "结束日期", required = false) String endDate,
                                       @ParameterObject Pagination pagination) {

        MPJLambdaWrapper<BizProjectRecord> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .selectAs(BizProjectRecord::getConstructTime,BizPulverizedCoalDaily::getConstructTime)
                .selectAs(BizWorkface::getWorkfaceName,BizPulverizedCoalDaily::getWorkfaceName)
                .selectAs(TunnelEntity::getTunnelName,BizPulverizedCoalDaily::getTunnelName)
                .selectAs(BizTravePoint::getPointName,BizPulverizedCoalDaily::getPointName)
                .selectCollection(BizDrillRecord.class,BizPulverizedCoalDaily::getDrillRecordList)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizProjectRecord::getProjectId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getWorkfaceId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId)
                .leftJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .eq(workfaceId != null ,BizProjectRecord::getWorkfaceId, workfaceId)
                .eq(tunnelId != null , BizProjectRecord::getTunnelId,tunnelId)
                .eq(StrUtil.isNotEmpty(constructType),BizProjectRecord::getConstructType,constructType)
                .between(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate),BizProjectRecord::getConstructTime,startDate,endDate);

        IPage<BizPulverizedCoalDaily> sss =  bizProjectRecordMapper.selectJoinPage(pagination, BizPulverizedCoalDaily.class,mpjLambdaWrapper);
        List<BizPulverizedCoalDaily> voList =  sss.getRecords();
        List<BizPulverizedCoalDaily> voList1 =  new ArrayList<>();
        Integer s = voList.size();
        for (int i = 0; i < s; i++) {
            BizPulverizedCoalDaily bizPulverizedCoalDaily = voList.get(i);
            String a =  bizPulverizedCoalDaily.getWorkfaceName() +
                    "-" + bizPulverizedCoalDaily.getTunnelName() +
                    "-"+ bizPulverizedCoalDaily.getPointName() +
                    "-" +bizPulverizedCoalDaily.getConstructRange();
            bizPulverizedCoalDaily.setConstructLocation(a);
            System.out.println("bizPulverizedCoalDaily = " + bizPulverizedCoalDaily);
            if(bizPulverizedCoalDaily != null && bizPulverizedCoalDaily.getDrillRecordList() != null && bizPulverizedCoalDaily.getDrillRecordList().size() > 0){
                BizDrillRecord drillRecord = bizPulverizedCoalDaily.getDrillRecordList().get(0);
                String crumStr = drillRecord.getCrumbWeight();
                bizPulverizedCoalDaily.setDrillRealDeep(drillRecord.getRealDeep()+"");
                if(StrUtil.isNotEmpty(crumStr) && !crumStr.equals("[]") && !crumStr.equals("\"\"")){
                    List<CrumbWeight> crumbWeights = JSONUtil.toList(crumStr, CrumbWeight.class);
                    double sum = crumbWeights.stream()
                            .mapToDouble(CrumbWeight::getValue1)
                            .sum();
                    Optional<CrumbWeight> maxPoint = crumbWeights.stream().max(Comparator.comparing(CrumbWeight::getValue1));
                    if (maxPoint.isPresent()) {
                        bizPulverizedCoalDaily.setCoalMax(maxPoint.get().getValue1()+"");
                        bizPulverizedCoalDaily.setCoalSum(sum+"");
                    }
                }
            }
            voList1.add(bizPulverizedCoalDaily);
        }
        sss.setRecords(null);
        sss.setRecords(voList1);

        return R.ok(new MPage<>(sss));
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
