package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/10
 * @description:
 */

@Data
public class DepartAuditHistoryDTO {

    @ApiModelProperty(value = "工程填报信息")
    private BizProjectRecordDetailVo bizProjectRecordDetailVo;

    @ApiModelProperty(value = "审核结果")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    private String rejectionReason;

    @ApiModelProperty(value = "审核人")
    private String reviewer;
}