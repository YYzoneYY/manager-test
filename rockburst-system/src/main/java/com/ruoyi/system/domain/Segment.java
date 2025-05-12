package com.ruoyi.system.domain;


import lombok.Getter;
import lombok.Setter;
import com.ruoyi.system.domain.Point2D;
import java.math.BigDecimal;

@Setter
@Getter
public class Segment {

    Point2D start;
    Point2D end;
    BigDecimal interval;
    Long areaId;

    public Segment(Point2D start, Point2D end, BigDecimal interval) {
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    public Segment(Point2D start, Point2D end, BigDecimal interval, Long areaId) {
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.areaId = areaId;
    }
}
