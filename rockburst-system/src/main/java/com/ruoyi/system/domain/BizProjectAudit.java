package com.ruoyi.system.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.io.Serializable;

/**
 * 工程填报审核记录对象 biz_project_audit
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
public class BizProjectAudit implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long projectAuditId;

    /** 工程填报id */
    @Excel(name = "工程填报id")
    private Long projectId;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private String status;

    /** 序号 */
    @Excel(name = "序号")
    private Long no;


}
