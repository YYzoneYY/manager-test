package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DropRule {
    private List<Double> drop_range;
    private double base_value;
    private List<AttenuationRule> attenuation;
}
