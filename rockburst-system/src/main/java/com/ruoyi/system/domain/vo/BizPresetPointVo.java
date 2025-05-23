package com.ruoyi.system.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 矿井危险区对象 BizDangerArea
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("导线点悬浮")
public class BizPresetPointVo extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long presetPointId;

    @ApiModelProperty(value = "巷道")
    private Long tunnelId;


    @ApiModelProperty(value = "组")
    private String axiss;

    @ApiModelProperty(value = "帮")
    private Long tunnelBarId;

    @ApiModelProperty(value = "危险区")
    private Long dangerAreaId;


    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;


    @ApiModelProperty(value = "实际位置 导线点")
    private Long pointId;


    @ApiModelProperty(value = "钻孔类型")
    private String drillType;


    @ApiModelProperty(value = "实际位置 前后距离")
    private Double meter;


    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "工程id")
    private Long projectId;


    @JSONField(
            format = "yyyy-MM-dd"
    )
    private Date constructTime;

    @ApiModelProperty(value = "纬度T")
    private String latitudet;

    @ApiModelProperty(value = "经度T")
    private String longitudet;



    @ApiModelProperty(value = "施工队")
    private String constructionUnit;

    @ApiModelProperty(value = "掘进回采")
    private String constructType;

    @ApiModelProperty(value = "组")
    private String latlngs;

    @ApiModelProperty(value = "迎头组")
    private String crosLatlngs;



    @ApiModelProperty(value = "工作面")
    private String workfaceName;
    @ApiModelProperty(value = "巷道")
    private String tunnelName;
    @ApiModelProperty(value = "导线点")
    private String pointName;

    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "实际深度")
    private String realDeep;

    @ApiModelProperty(value = "计划深度")
    private String planDeep;

    @ApiModelProperty(value = "施工员")
    private String worker;

    @ApiModelProperty(value = "施工负责人")
    private String projecrHeader;

    @ApiModelProperty(value = "安检员")
    private String securityer;

    @ApiModelProperty(value = "爆破员")
    private String bigbanger;

    @ApiModelProperty(value = "验收员")
    private String accepter;

    @ApiModelProperty(value = "危险区等级")
    private String level;

    @ApiModelProperty(value = "颜色")
    private String color;

}
