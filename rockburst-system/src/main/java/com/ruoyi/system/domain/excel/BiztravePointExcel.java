package com.ruoyi.system.domain.excel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author ruoyi
 * @date 2024-11-09
 */

@Setter
@Getter
@Accessors(chain = true)
public class BiztravePointExcel
{
    private static final long serialVersionUID = 1L;


    /** 矿井的唯一标识符 */
    @Excel(name = "巷道")
    private String tunnelName;
//    @Excel(name = "巷道", cellType = Excel.ColumnType.NUMERIC)

    /** 矿井名称 */
    @Excel(name = "导线点名称")
    private String pointName;

    /** 所属省份 */
    @Excel(name = "坐标x")
    private String axisx;

    /** 所属省份 */
    @Excel(name = "坐标y")
    private String axisy;
    /** 所属省份 */
    @Excel(name = "坐标z")
    private String axisz;

    @Excel(name = "经度")
    private String longitude;

    @Excel(name = "纬度")
    private String latitude;


    @Excel(name = "编号")
    private Long no;


    @Excel(name = "距前导线点距离")
    private Double prePointDistance;

    @ApiModelProperty(value = "0 起始点  1 结束点 2 普通点")
    @TableField()
    private Integer isVertex;

    @ApiModelProperty(value = "距前导线点距离")
    @TableField()
    private Double afterPointDistance;

//    @Excel(name = "切眼标记点id")
//    private Long bestNearPointId;
//
//    @Excel(name = "距标记点距离")
//    private Double distance;

}
