package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningSelectDTO;
import com.ruoyi.system.service.MiningFootageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/14
 * @description:
 */

@Api(tags = "回采进尺管理")
@RestController
@RequestMapping(value = "/miningFootage")
public class MiningFootageController {

    @Resource
    private MiningFootageService miningFootageService;

    @ApiOperation(value = "新增回采进尺", notes = "新增回采进尺")
    @PostMapping("/add")
    public R<MiningFootageDTO> add(@RequestBody @Validated(value = {ParameterValidationAdd.class, ParameterValidationOther.class}) MiningFootageDTO miningFootageDTO){
        return R.ok(miningFootageService.insertMiningFootage(miningFootageDTO),"新增成功");
    }

    @ApiOperation(value = "回采进尺修改", notes = "回采进尺修改")
    @PutMapping("/update")
    public R<MiningFootageEntity> update(@RequestBody @Validated(value = {ParameterValidationUpdate.class, ParameterValidationOther.class}) MiningFootageDTO miningFootageDTO){
        return R.ok(miningFootageService.updateMiningFootage(miningFootageDTO),"修改成功");
    }

    @ApiOperation(value = "回采进尺擦除", notes = "回采进尺擦除")
    @DeleteMapping(value = "/clear")
    public R<Integer> clear(@RequestBody MiningFootageDTO miningFootageDTO){
        return R.ok(miningFootageService.clear(miningFootageDTO),"删除成功");
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryList")
    public R<TableData> queryList(@RequestBody MiningSelectDTO miningSelectDTO,
                                  @RequestParam(required = false) Integer pageNum,
                                  @RequestParam(required = false) Integer pageSize){
        return R.ok(miningFootageService.pageQueryList(miningSelectDTO,pageNum,pageSize));
    }

    @ApiOperation(value = "查询是否有时间相同", notes = "查询是否有时间相同")
    @GetMapping(value = "/queryByTime")
    public R<String> queryByTime(@RequestParam(value = "miningTime") Long miningTime,
                                 @RequestParam(value = "workfaceId") Long workfaceId){
        return R.ok(miningFootageService.queryByTime(miningTime,workfaceId));
    }

    @ApiOperation(value = "获取剩余工作面长度", notes = "获取剩余工作面长度")
    @GetMapping(value = "/getSurplusLength")
    public R<Object> getSurplusLength(@RequestParam(value = "workfaceId") Long workfaceId){
        return R.ok(miningFootageService.getSurplusLength(workfaceId));
    }
}