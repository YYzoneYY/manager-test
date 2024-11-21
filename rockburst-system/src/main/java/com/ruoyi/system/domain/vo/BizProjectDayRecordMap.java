package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */

@Setter
@Getter
@Accessors(chain = true)
public class BizProjectDayRecordMap
{
    private static final long serialVersionUID = 1L;


    /** 施工时间 */
    @ApiModelProperty(value = "projectId")
    private Long projectId;
    /** 施工时间 */
    @ApiModelProperty(value = "no")
    private Integer no;

    @ApiModelProperty(value = "矜持")
    private BigDecimal minePace;

    @ApiModelProperty(value = "施工地点id")
    private Long tunnelId;

    @ApiModelProperty(value = "施工地点")
    private Long workfaceName;

    /** 钻孔编号 */
    @ApiModelProperty(value = "方向")
    private String direction;

    @ApiModelProperty(value = "深度")
    private BigDecimal  realDeep;


    @ApiModelProperty(value = "导向点")
    private String pointName;


    @ApiModelProperty(value = "距离")
    private String constructRange;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private String worker;

    @ApiModelProperty(name = "施工单位")
    private String constructUnitName;

    @ApiModelProperty(name = "施工单位")
    private Long constructUnitId;

    @ApiModelProperty(value = "备注")
    private String remark;

}
