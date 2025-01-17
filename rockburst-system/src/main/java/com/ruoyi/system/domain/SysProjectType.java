package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 岗位表 sys_post
 * 
 * @author ruoyi
 */
@Getter
@Setter
public class SysProjectType extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 矿井的唯一标识符 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 岗位编码 */
    @TableField
    private String value;

    /** 岗位名称 */
    @TableField
    private String lable;

    /** 岗位排序 */
    @TableField
    private String img;

    @TableField
    private Integer sort;

    @TableField
    private String must;

    @TableField
    private String noMust;

    @TableField(exist = false)
    private Map<String, List<String>> keySet;


}
