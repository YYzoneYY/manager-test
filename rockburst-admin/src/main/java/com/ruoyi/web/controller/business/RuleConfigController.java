package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.RuleConfigDTO;
import com.ruoyi.system.domain.dto.project.AddRuleConfigDTO;
import com.ruoyi.system.service.RuleConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Api(tags = "规则配置")
@RestController
@RequestMapping(value = "/ruleConfig")
public class RuleConfigController {

    @Resource
    private RuleConfigService ruleConfigService;

    @ApiOperation(value = "新增规则配置", notes = "新增规则配置")
    @PostMapping(value = "/insert")
    public R<Object> insert(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) AddRuleConfigDTO addRuleConfigDTO) {
        return R.ok(this.ruleConfigService.insert(addRuleConfigDTO));
    }

    @ApiOperation(value = "修改规则配置", notes = "修改规则配置")
    @PutMapping(value = "/update")
    public R<Object> update(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) AddRuleConfigDTO addRuleConfigDTO) {
        return R.ok(this.ruleConfigService.update(addRuleConfigDTO));
    }

    @ApiOperation(value = "获取规则配置列表", notes = "获取规则配置列表")
    @GetMapping(value = "/getRuleConfigList")
    public R<List<RuleConfigDTO>> getRuleConfigList() {
        return R.ok(this.ruleConfigService.getRuleConfigList());
    }
}