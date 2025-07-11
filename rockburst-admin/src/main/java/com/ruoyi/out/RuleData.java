package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RuleData {

    private String ruleName;

    private List<DeepDropRuleGroup> rules;

    private List<RangeRule> ruleData;
}
