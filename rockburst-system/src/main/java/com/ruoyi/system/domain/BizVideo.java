package com.ruoyi.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 工程视频对象 biz_video
 * 
 * @author ruoyi
 * @date 2024-11-09
 */

@Getter
@Setter
@Accessors(chain = true)
public class BizVideo extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long videoId;

    /** 文件大小 */
    @ApiModelProperty(name = "文件大小")
    private String fileSize;

    /** 文件名 */
    @ApiModelProperty(name = "文件名")
    private String fileName;

    /** 存放位置 */
    @ApiModelProperty(name = "存放位置")
    private String bucket;

    /** 视频地址 */
    @ApiModelProperty(name = "视频地址")
    private String fileUrl;

    /** 参数 */
    @ApiModelProperty(name = "参数")
    private String param;

    /** 工程id */
    @ApiModelProperty(name = "工程id")
    private Long projectId;


    /** 工程id */
    @ApiModelProperty(name = "状态")
    private String status;


    /** 工程id */
    @ApiModelProperty(name = "任务id")
    private String taskId;


    /** 工程id */
    @ApiModelProperty(name = "类型")
    private String type;


    /** 工程id */
    @ApiModelProperty(name = "子视频")
    private String aiFileUrl;

    /** 工程id */
    @ApiModelProperty(name = "子视频首页")
    private String aiFileImageUrl;

}
