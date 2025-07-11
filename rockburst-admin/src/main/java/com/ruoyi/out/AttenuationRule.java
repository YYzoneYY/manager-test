package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttenuationRule {
    private List<Double> meter_range;
    private String start;
    private String end; // 可以是 String 或 Float
}
