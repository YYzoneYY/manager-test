package com.ruoyi.system.domain.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
@Accessors(chain = true)
public class BizProjectDayRecordExcel
{
    /** 施工时间 */
    @ApiModelProperty(value = "projectId")
    private Long projectId;
    /** 施工时间 */
    @ApiModelProperty(value = "no")
    private Integer no;

    @ApiModelProperty(value = "矜持")
    private BigDecimal minePace;

    @ApiModelProperty(value = "施工地点")
    private Long workfaceName;

    /** 钻孔编号 */
    @ApiModelProperty(value = "采帮")
    private String caibang;

    @ApiModelProperty(value = "非采帮")
    private String feicaibang;

    @ApiModelProperty(value = "正迎头")
    private String zhengyingtiou;

    @ApiModelProperty(value = "实际施工数量（个）")
    private String geshu;

    @ApiModelProperty(value = "钻孔总深度（m）")
    private String zongshen;


    @ApiModelProperty(value = "采帮location")
    private String caibanglocation;

    @ApiModelProperty(value = "非采帮location")
    private String feicaibanglocation;

    @ApiModelProperty(value = "正迎头location")
    private String zhengyingtioulocation;


    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private String worker;

    @ApiModelProperty(name = "施工单位")
    private String constructUnitName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
