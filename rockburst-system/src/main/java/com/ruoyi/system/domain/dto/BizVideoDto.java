package com.ruoyi.system.domain.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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



    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long videoId;

    /** 文件大小 */

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    /** 文件名 */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /** 存放位置 */
    @ApiModelProperty(value = "存放位置")
    private String bucket;

    /** 视频地址 */
    @ApiModelProperty(value = "视频地址")
    private String fileUrl;

    /** 参数 */
    @ApiModelProperty(value = "参数")
    private String param;

    /** 工程id */
    @ApiModelProperty(value = "工程id")
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
    private Long childId;

}
