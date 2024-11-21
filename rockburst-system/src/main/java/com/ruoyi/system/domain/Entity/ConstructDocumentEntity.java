package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import com.ruoyi.system.domain.dto.NewTreeEntity;
import com.ruoyi.system.domain.dto.TreeListEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */

@Data
@ApiModel("施工文档表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("construct_document")
public class ConstructDocumentEntity extends BusinessBaseEntity implements Serializable, NewTreeEntity<Long, Long>, TreeListEntity<Long, Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "data_id", type = IdType.AUTO)
    private Long dataId;

    @ApiModelProperty(value = "层级名称")
    @TableField(value = "level_name")
    private String levelName;

    @ApiModelProperty(value = "上一级id")
    @TableField(value = "super_id")
    private Long superId;

    @ApiModelProperty(value = "文件名称")
    @TableField(value = "document_name")
    private String documentName;

    @ApiModelProperty(value = "文件id")
    @TableField(value = "file_id")
    private Long fileId;

    @ApiModelProperty(value = "级别")
    @TableField(value = "level")
    private Integer level;

    @ApiModelProperty(value = "排序")
    @TableField(value = "sort")
    private Long sort;

//    @ApiModelProperty(value = "创建时间")
//    @TableField(value = "create_time")
//    private Long createTime;

    @ApiModelProperty(value = "标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;

    @Override
    public Long getValue() {
        return dataId;
    }

    @Override
    public String getLabel() {
        return levelName;
    }

    @Override
    public Long fileId() {
        return fileId;
    }

    @Override
    public String documentName() {
        return documentName;
    }

    @Override
    public Long createTime() {
        return getCreateTime();
    }


    @Override
    public Integer level() {
        return level;
    }

    @Override
    public Long sort() {
        return sort;
    }

    @Override
    public boolean isDisable() {
        return this.getSuperId() == null;
    }
}