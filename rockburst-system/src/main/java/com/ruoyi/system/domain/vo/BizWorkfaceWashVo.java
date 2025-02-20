package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.yulichang.annotation.EntityMapping;
import com.ruoyi.system.domain.BizWorkface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 工作面管理对象 biz_workface
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Getter
@Setter
public class BizWorkfaceWashVo extends BizWorkface
{


    @ApiModelProperty(value = "危险区域")
    @TableField(exist = false)
    @EntityMapping(tag = BizTunnelVo.class ,thisField = "workfaceId" , joinField = "workFaceId" )
    private List<BizDangerAreaVo> tunnels;





}
