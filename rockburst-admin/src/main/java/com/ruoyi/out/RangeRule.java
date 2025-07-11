package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RangeRule {
    private List<Double> range;
    private Double start;
    private Double end;
}
