package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.BizProjectRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class BizProjectRecordMap  {

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    @ApiModelProperty(value = "施工班次")
    private String constructShiftName;

    /** 钻孔编号 */
    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "工作面")
    private String workFaceName;

    @ApiModelProperty(value = "leixing")
    private String constructType;


    @ApiModelProperty(value = "导向点")
    private String pointName;


    @ApiModelProperty(value = "距离")
    private String constructRange;

    @ApiModelProperty(name = "实际深度")
    private String realDeep;

    @ApiModelProperty(name = "钻孔直径")
    private String diameter;

    @ApiModelProperty(value = "施工负责人")
    private String projecrHeader;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private String worker;

    /** 安检员 */
    @ApiModelProperty(value = "安检员")
    private String securityer;

    @ApiModelProperty(name = "工具")
    private String borer;

    @ApiModelProperty(value = "备注")
    private String remark;



}
