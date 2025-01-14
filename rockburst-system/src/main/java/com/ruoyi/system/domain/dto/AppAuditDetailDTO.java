package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/14
 * @description:
 */

@Data
public class AppAuditDetailDTO {

    @ApiModelProperty(value = "工程填报信息")
    private BizProjectRecordDetailVo bizProjectRecordDetailVo;

    @ApiModelProperty(value = "审核结果")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    private String rejectionReason;

    @ApiModelProperty(value = "审核人")
    private String reviewer;

    @ApiModelProperty(value = "审核结果格式化")
    private String auditResultFmt;
}