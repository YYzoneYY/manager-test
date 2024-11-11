package com.ruoyi.system.domain;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 工程视频对象 biz_video
 * 
 * @author ruoyi
 * @date 2024-11-09
 */

@Getter
@Setter
public class BizVideo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long videoId;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private String fileSize;

    /** 文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 存放位置 */
    @Excel(name = "存放位置")
    private String bucket;

    /** 视频地址 */
    @Excel(name = "视频地址")
    private String fileUrl;

    /** 参数 */
    @Excel(name = "参数")
    private String param;

    /** 工程id */
    @Excel(name = "工程id")
    private Long projectId;

}
