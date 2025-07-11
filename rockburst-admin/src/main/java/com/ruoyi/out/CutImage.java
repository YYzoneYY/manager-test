package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CutImage {
    List<String> shapes;
    List<BigDecimal> points;
}
