package com.ruoyi.system.domain.excel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */

@Setter
@Accessors(chain = true)
public class BizProjectRecordExcel
{
    private static final long serialVersionUID = 1L;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    @ApiModelProperty(value = "施工班次")
    private String constructShiftName;

    /** 钻孔编号 */
    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "工作面")
    private String workFaceName;

    @ApiModelProperty(value = "leixing")
    private String constructType;


    @ApiModelProperty(value = "导向点")
    private String pointName;


    @ApiModelProperty(value = "距离")
    private String constructRange;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(name = "实际深度")
    private String realDeep;

    @ApiModelProperty(name = "钻孔直径")
    private String diameter;

    @ApiModelProperty(value = "施工负责人")
    private String projecrHeader;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private String worker;

    /** 安检员 */
    @ApiModelProperty(value = "安检员")
    private String securityer;

    @ApiModelProperty(name = "工具")
    private String borer;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Date getConstructTime() {
        return constructTime;
    }

    public String getConstructShiftName() {
        return constructShiftName;
    }

    public String getDrillNum() {
        return drillNum;
    }


    public String getLocation() {
        if("huicai".equals(constructType)){
            return "回采" + (workFaceName == null ? "":workFaceName)  + "(" + constructRange+")";
        }
        return (pointName == null ? "":pointName) + "(" + constructRange+")";
    }




    public String getRealDeep() {
        return realDeep;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getProjecrHeader() {
        return projecrHeader;
    }

    public String getWorker() {
        return worker;
    }

    public String getSecurityer() {
        return securityer;
    }

    public String getBorer() {
        return borer;
    }

    public String getRemark() {
        return remark;
    }
}
