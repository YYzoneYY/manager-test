package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 工程填报审核记录对象 biz_project_audit
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
@Accessors(chain = true)
public class BizProjectAudit extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long auditId;

    /** 工程填报id */
    @ApiModelProperty(name = "工程填报id")
    private Long projectId;

    /** 序号 */
    @ApiModelProperty(name = "序号")
    private Integer no;

    /** 审核状态 */
    @ApiModelProperty(name = "审核状态 通过 1 不通过 0")
    private Integer status;

    @ApiModelProperty(name = "标识")
    private String tag;

    @ApiModelProperty(name = "驳回原因")
    private String rejectionReason;




}
