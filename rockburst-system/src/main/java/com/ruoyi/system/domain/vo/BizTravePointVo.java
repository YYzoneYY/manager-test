package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井管理对象 biz_mine
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("导线点管理对象")
public class BizTravePointVo
{

    /** 矿井的唯一标识符 */
    @TableId( type = IdType.AUTO)
    private Long pointId;

    /** 矿井的唯一标识符 */
    @ApiModelProperty(value = "工作面id")
    @TableField()
    private Long workfaceId;


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


    @ApiModelProperty(value = "状态")
    @TableField()
    private Integer status;

    @ApiModelProperty(value = "编号")
    @TableField()
    private String no;

    @ApiModelProperty(value = "是否使用过")
    @TableField(exist = false)
    private Boolean did;






}
