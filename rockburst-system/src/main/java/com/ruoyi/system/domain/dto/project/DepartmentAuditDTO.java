package com.ruoyi.system.domain.dto.project;

import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/3
 * @description:
 */
@Data
public class DepartmentAuditDTO {

    @ApiModelProperty(value = "工程填报信息")
    private BizProjectRecordDetailVo projectRecordDetailVo;

    @ApiModelProperty(value = "区队提报人员")
    private String teamAuditPeople;
}