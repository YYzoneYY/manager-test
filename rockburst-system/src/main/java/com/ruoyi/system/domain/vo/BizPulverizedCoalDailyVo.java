package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.BizDrillRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BizPulverizedCoalDailyVo  {



    @ApiModelProperty(value = "钻孔类型")
    private String drillType;
    
    /** 距离 */
    @ApiModelProperty(value = "距离")
    private String constructRange;

    @ApiModelProperty(value = "施工类型 回踩 掘进")
    private String constructType;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;

    /** 施工地点 */
    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "帮id")
    private Long barId;

    @ApiModelProperty(value = "导线点id")
    private Long travePointId;

    @ApiModelProperty(value = "工作面")
    private String workfaceName;

    @ApiModelProperty(value = "巷道")
    private String tunnelName;

    @ApiModelProperty(value = "帮名称")
    private String barName;

    @ApiModelProperty(value = "导线点名称")
    private String pointName;

    List<BizDrillRecord> drillRecords;


}
