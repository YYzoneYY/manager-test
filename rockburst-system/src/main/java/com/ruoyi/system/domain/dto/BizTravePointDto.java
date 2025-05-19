package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.system.constant.GroupAdd;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 矿井管理对象 biz_mine
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
public class BizTravePointDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 矿井的唯一标识符 */
    @TableId( type = IdType.AUTO)
    private Long pointId;

    /** 矿井的唯一标识符 */
    @ApiModelProperty(value = "工作面id")
    @TableField()
    private Long workfaceId;

    /** 矿井的唯一标识符 */
    @ApiModelProperty(value = "巷道")
    @TableField()
    private Long tunnelId;

    /** 矿井的唯一标识符 */
    @ApiModelProperty(value = "巷道name")
    private String tunnelName;

    /** 矿井名称 */
    @ApiModelProperty(value = "导线点名称")
    @TableField()
    private String pointName;

    /** 所属省份 */
    @ApiModelProperty(value = "坐标x")
    @TableField()
    private String axisx;

    /** 所属省份 */
    @ApiModelProperty(value = "坐标y")
    @TableField()
    private String axisy;

    /** 所属省份 */
    @ApiModelProperty(value = "坐标z")
    @TableField()
    private String axisz;


//    @ApiModelProperty(value = "状态")
//    @TableField()
//    private Integer status;

    @ApiModelProperty(value = "编号")
    @TableField()
    private Long no;

    @ApiModelProperty(value = "前导线点id")
    @TableField()
    private Long prePointId;

    @ApiModelProperty(value = "距前导线点距离")
    @TableField()
    private Double prePointDistance;

    @ApiModelProperty(value = "顶点 1 是 0 否")
    @TableField()
    private Boolean isVertex;


    @ApiModelProperty(value = "切眼标记点id")
    @TableField()
    private Long bestNearPointId;

    @ApiModelProperty(value = "距标记点距离")
    @TableField()
    private Double distance;


    @ApiModelProperty(value = "纬度")
    private String latitude;

    @NotNull(groups = GroupAdd.class)
    @ApiModelProperty(value = "经度")
    private String longitude;



}
