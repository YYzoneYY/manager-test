package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.service.*;
import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Delete;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/14
 * @description:
 */

@Api(tags = "回采进尺管理")
@RestController
@RequestMapping(value = "/miningFootage")
public class MiningFootageController {

//    @Resource
//    private MiningFootageService miningFootageService;
//
//    @Resource
//    private MiningRecordService miningRecordService;

    @Resource
    private MiningService miningService;

    @Resource
    private MiningRecordNewService miningRecordNewService;

    @Resource
    private TunnelService tunnelService;

    @ApiOperation(value = "新增回采进尺", notes = "新增回采进尺")
    @PostMapping("/add")
    public R<Object> add(@RequestBody @Validated(value = {ParameterValidationAdd.class, ParameterValidationOther.class}) MiningFootageNewDTO miningFootageNewDTO){
        return R.ok(miningService.insertMiningFootage(miningFootageNewDTO),"新增成功");
    }

    @ApiOperation(value = "回采进尺修改", notes = "回采进尺修改")
    @PutMapping("/update")
    public R<Object> update(@RequestBody @Validated(value = {ParameterValidationUpdate.class}) MiningFootageNewDTO miningFootageNewDTO){
        return R.ok(miningService.updateMiningFootage(miningFootageNewDTO),"修改成功");
    }

    @ApiOperation(value = "回采进尺擦除", notes = "回采进尺擦除")
    @DeleteMapping(value = "/clear")
    public R<Integer> clear(@RequestBody MiningFootageNewDTO miningFootageNewDTO){
        return R.ok(miningService.clear(miningFootageNewDTO),"擦除成功");
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryList")
    public R<TableData> queryList(@RequestBody MiningSelectNewDTO miningSelectNewDTO,
                                  @ApiParam(value = "展示方式", required = true) @RequestParam String displayForm,
                                  @RequestParam(required = false) Integer pageNum,
                                  @RequestParam(required = false) Integer pageSize){
        return R.ok(miningService.pageQueryList(miningSelectNewDTO, displayForm, pageNum,pageSize));
    }

    @ApiOperation(value = "查询是否有时间相同", notes = "查询是否有时间相同")
    @GetMapping(value = "/queryByTime")
    public R<String> queryByTime(@RequestParam(value = "miningTime") Long miningTime,
                                 @RequestParam(value = "tunnelId") Long tunnelId){
        return R.ok(miningService.queryByTime(miningTime,tunnelId));
    }

    @ApiOperation(value = "获取剩余巷道长度", notes = "获取剩余巷道长度")
    @GetMapping(value = "/getSurplusLength")
    public R<Object> getSurplusLength(@RequestParam(value = "workfaceId") Long workfaceId,
                                      @RequestParam(value = "tunnelId") Long tunnelId){
        return R.ok(miningService.getSurplusLength(workfaceId, tunnelId));
    }

    @ApiOperation(value = "根据回采进尺id查询回采进尺记录", notes = "根据回采进尺id查询回采进尺记录")
    @GetMapping(value = "/getMiningRecord")
    public R<List<MiningRecordNewDTO>> queryByMiningRecordId(@RequestParam(value = "miningFootageId") Long miningFootageId){
        return R.ok(this.miningRecordNewService.queryByMiningRecordId(miningFootageId));
    }

    @ApiOperation(value = "获取巷道下拉框", notes = "获取巷道下拉框")
    @GetMapping(value = "/getTunnelChoiceList")
    public R<List<TunnelChoiceListDTO>> getTunnelChoiceList(@ApiParam(name = "faceId", value = "工作面id", required = true)
                                                            @RequestParam Long faceId){
        return R.ok(tunnelService.getTunnelChoiceListTwo(faceId));
    }

    @ApiOperation(value = "展示方式下拉框", notes = "展示方式下拉框")
    @GetMapping(value = "/getShowWayChoiceList")
    public R<List<ShowWayChoiceListDTO>> getShowWayChoiceList(@RequestParam(value = "workFaceId") Long workFaceId) {
        return R.ok(miningService.getShowWayChoiceList(workFaceId));
    }
}