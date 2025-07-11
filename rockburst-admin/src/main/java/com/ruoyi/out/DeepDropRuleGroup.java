package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeepDropRuleGroup {
    private List<Double> deep_range;
    private List<DropRule> drop_rules;
}
