package com.ruoyi.system.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("cad实体")
public class PolylineObject {

    @ApiModelProperty(value = "id")
    @TableField()
    private String id;
    @ApiModelProperty(value = "对象ID")
    @TableField()
    private String objectid;
    @ApiModelProperty(value = "实体名称")
    @TableField()
    private String name;
    @ApiModelProperty(value = "透明度")
    @TableField()
    private Integer alpha;
    @ApiModelProperty(value = "面积")
    @TableField()
    private Double area;
    @ApiModelProperty(value = "边界坐标")
    @TableField()
    private String bounds;
    @ApiModelProperty(value = "凸度数组")
    @TableField()
    private String bulge;
    @ApiModelProperty(value = "ARGB颜色")
    @TableField()
    private Integer color;
    @ApiModelProperty(value = "颜色索引")
    @TableField()
    private Integer colorIndex;
    @ApiModelProperty(value = "三维数据")
    @TableField()
    private String data3d;
    @ApiModelProperty(value = "高程")
    @TableField()
    private Double elevation;
    @ApiModelProperty(value = "包络范围")
    @TableField()
    private String envelop;
    @ApiModelProperty(value = "GeoJSON结构")
    @TableField()
    private String geojson;
    @ApiModelProperty(value = "是否是包络")
    @TableField()
    private Boolean isEnvelop;
    @ApiModelProperty(value = "是否闭合")
    @TableField()
    private Boolean isClosed;
    @ApiModelProperty(value = "图层索引")
    @TableField()
    private Integer layerIndex;
    @ApiModelProperty(value = "线型缩放")
    @TableField()
    private Double lineTypeScale;
    @ApiModelProperty(value = "线宽")
    @TableField()
    private Double lineWidth;
    @ApiModelProperty(value = "线型")
    @TableField()
    private String linetype;
    @ApiModelProperty(value = "线型缩放")
    @TableField()
    private Double linetypeScale;
    @ApiModelProperty(value = "点坐标集合")
    @TableField()
    private String points;
    @ApiModelProperty(value = "厚度")
    @TableField()
    private Double thickness;
    @ApiModelProperty(value = "扩展数据")
    @TableField()
    private String xdata;

    @ApiModelProperty(value = "开始x")
    @TableField()
    private String startx;

    @ApiModelProperty(value = "开始y")
    @TableField()
    private String starty;

    @ApiModelProperty(value = "结束x")
    @TableField()
    private String endx;

    @ApiModelProperty(value = "结束y")
    @TableField()
    private String endy;

    @TableField(exist = false)
    Point2D start;
    @TableField(exist = false)
    Point2D end;
}
