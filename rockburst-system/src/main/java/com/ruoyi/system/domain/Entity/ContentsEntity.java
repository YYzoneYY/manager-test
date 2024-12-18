package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.system.domain.dto.ContentsTreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/12/7
 * @description:
 */

@Data
@ApiModel("目录")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("contents")
public class ContentsEntity implements Serializable, ContentsTreeEntity<Long, Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "目录id")
    @TableId(value = "contents_id", type = IdType.AUTO)
    private Long contentsId;

    @ApiModelProperty(value = "目录名称")
    @TableField("contents_name")
    private String contentsName;

    @ApiModelProperty(value = "父级目录id")
    @TableField(value = "super_id")
    private Long superId;

    @Override
    public Long getValue() {
        return contentsId;
    }

    @Override
    public String getLabel() {
        return contentsName;
    }

    @Override
    public boolean isDisable() {
        return this.superId == null;
    }
}