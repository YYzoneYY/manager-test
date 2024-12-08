package com.ruoyi.system.domain.excel;

import lombok.Data;

import java.util.List;


@Data
public class ChartDataAll {

    List<ChartData> chartDataList;

    String title;
}
