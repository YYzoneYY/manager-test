package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.system.domain.BizTunnelBar;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井危险区对象 BizDangerArea
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("矿井危险区对象")
public class BizTunnelBarVo extends BizTunnelBar
{

    @ApiModelProperty(value = "工作面名称")
    @TableField(exist = false)
//    @EntityMapping(tag = BizWorkface.class ,thisField = "workfaceId" , joinField = "workfaceId", select = "workfaceName" )
    private String workfaceName;

    @ApiModelProperty(value = "巷道名称")
    @TableField(exist = false)
//    @FieldMapping(tag = TunnelEntity.class ,thisField = "tunnelId" , joinField = "tunnelId" , select = "tunnelName")
    private String tunnelName;

    @ApiModelProperty(value = "起始导线点名称")
    @TableField(exist = false)
//    @EntityMapping(tag = BizTravePoint.class ,thisField = "pointId" , joinField = "startPointId", select = "pointName" )
    private String startPointName;

    @ApiModelProperty(value = "结束导线点名称")
    @TableField(exist = false)
//    @EntityMapping(tag = BizTravePoint.class ,thisField = "pointId" , joinField = "endPointId", select = "pointName" )
    private String endPointName;


}
