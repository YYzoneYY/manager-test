package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.BizDrillRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BizPulverizedCoalDaily {


    @ApiModelProperty(value = "施工日期")
    private String constructTime;

    @ApiModelProperty(value = "施工地点")
    private String constructLocation;

    @ApiModelProperty(value = "钻孔记录")
    private List<BizDrillRecord> drillRecordList;

    @ApiModelProperty(value = "施工地点")
    private String constructRange;


    @ApiModelProperty(value = "工作面")
    private String workfaceName;

    @ApiModelProperty(value = "巷道")
    private String tunnelName;

    @ApiModelProperty(value = "导线点")
    private String pointName;

    @ApiModelProperty(value = "煤粉量合计")
    private String coalSum;

    @ApiModelProperty(value = "最大煤粉量")
    private String coalMax;

    @ApiModelProperty(value = "钻孔深度")
    private String drillRealDeep;




}
