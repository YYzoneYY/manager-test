package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ExcavationFootageEntity;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationRecordDTO;
import com.ruoyi.system.domain.dto.ExcavationSelectDTO;
import com.ruoyi.system.service.ExcavationFootageService;
import com.ruoyi.system.service.ExcavationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */

@Api(tags = "掘进进尺管理")
@RestController
@RequestMapping(value = "/excavationFootage")
public class ExcavationFootageController {

    @Resource
    private ExcavationFootageService excavationFootageService;

    @Resource
    private ExcavationRecordService excavationRecordService;

    @ApiOperation(value = "新增掘进进尺", notes = "新增掘进进尺")
    @PostMapping("/add")
    public R<ExcavationFootageDTO> add(@RequestBody @Validated(value = {ParameterValidationAdd.class, ParameterValidationOther.class})
                                       ExcavationFootageDTO excavationFootageDTO) {
        return R.ok(this.excavationFootageService.insertExcavationFootage(excavationFootageDTO));
    }

    @ApiOperation(value = "修改掘进进尺", notes = "修改掘进进尺")
    @PutMapping("/update")
    public R<ExcavationFootageEntity> update(@RequestBody @Validated(value = {ParameterValidationUpdate.class})
                                           ExcavationFootageDTO excavationFootageDTO) {
        return R.ok(this.excavationFootageService.updateExcavationFootage(excavationFootageDTO));
    }

    @ApiOperation(value = "掘进进尺擦除", notes = "掘进进尺擦除")
    @DeleteMapping("/clear")
    public R<Integer> clear(@RequestBody ExcavationFootageDTO excavationFootageDTO) {
        return R.ok(this.excavationFootageService.clear(excavationFootageDTO));
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryList")
    public R<TableData> queryList(@RequestBody ExcavationSelectDTO excavationSelectDTO,
                                  @RequestParam(required = false) Integer pageNum,
                                  @RequestParam(required = false) Integer pageSize) {
        return R.ok(this.excavationFootageService.pageQueryList(excavationSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "查询是否有时间相同", notes = "查询是否有时间相同")
    @GetMapping(value = "/queryByTime")
    public R<String> queryByTime(@RequestParam(value = "excavationTime") Long excavationTime,
                                 @RequestParam(value = "tunnelId") Long tunnelId) {
        return R.ok(this.excavationFootageService.queryByTime(excavationTime, tunnelId));
    }

    @ApiOperation(value = "获取剩余巷道长度", notes = "获取剩余巷道长度")
    @GetMapping(value = "/getSurplusLength")
    public R<Object> getSurplusLength(@RequestParam(value = "tunnelId") Long tunnelId) {
        return R.ok(this.excavationFootageService.getSurplusLength(tunnelId));
    }

    @ApiOperation(value = "根据掘进进尺id查询掘进记录", notes = "根据掘进进尺id查询掘进记录")
    @GetMapping(value = "/getExcavationRecordId")
    public R<List<ExcavationRecordDTO>> queryByExcavationRecordId(@RequestParam(value = "excavationFootageId") Long excavationFootageId) {
        return R.ok(this.excavationRecordService.queryByExcavationRecordId(excavationFootageId));
    }
}