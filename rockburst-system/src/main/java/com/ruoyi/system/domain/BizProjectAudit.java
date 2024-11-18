package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.security.core.parameters.P;

import java.io.Serializable;

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
    private Long projectAuditId;

    /** 工程填报id */
    @ApiModelProperty(name = "工程填报id")
    private Long projectId;

    /** 审核状态 */
    @ApiModelProperty(name = "审核状态")
    private Integer status;


    @ApiModelProperty(name = "审核状态")
    private String msg;

    @ApiModelProperty(name = "审核状态")
    private String level;

    /** 序号 */
    @ApiModelProperty(name = "序号")
    private Long no;


}
