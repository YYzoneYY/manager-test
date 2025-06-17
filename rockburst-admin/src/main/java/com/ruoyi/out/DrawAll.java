package com.ruoyi.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DrawAll {
    List<Draw> draws;
    private Double xmin;
    private Double ymin;
    private Double xmax;
    private Double ymax;
    private Double multiple;
    private Integer startLevel;
    private Integer endLevel;
    private Integer level;
}
