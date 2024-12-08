package com.ruoyi.system.domain.excel;

import lombok.Data;

import java.util.List;


@Data
public class ChartData {

    String title;

    List<Double> data;
}
