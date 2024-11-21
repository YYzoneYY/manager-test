package com.ruoyi.system.domain.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 工程视频对象 biz_video
 * 
 * @author ruoyi
 * @date 2024-11-09
 */

@Getter
@Setter
public class BizVideoDto
{
    private static final long serialVersionUID = 1L;

    /** 文件名 */
    @ApiModelProperty(name = "文件名")
    private String fileName;

}
