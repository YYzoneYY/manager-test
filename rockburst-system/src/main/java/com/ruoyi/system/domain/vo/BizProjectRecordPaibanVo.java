package com.ruoyi.system.domain.vo;

import com.ruoyi.system.constant.BizBaseConstant;
import lombok.Setter;

import java.util.Date;

@Setter
public class BizProjectRecordPaibanVo  {


    private Long constructionUnitId;
    private String constructionUnitName;
    private String worker;
    private String drillNum;
    private Date constructTime;
    private String steelBeltStart;
    private String steelBeltEnd;
    private String remark;
    private String realDeep;
    private String diameter;
    private String tunnelName;
    private String workfaceName;
    private String constructLocation;
    private String type;

    public String getConstructLocation() {
        if (BizBaseConstant.CONSTRUCT_TYPE_J.equals(type)){
            return tunnelName;
        }
        return workfaceName;
    }

    public Long getConstructionUnitId() {
        return constructionUnitId;
    }

    public String getConstructionUnitName() {
        return constructionUnitName;
    }

    public String getWorker() {
        return worker;
    }

    public String getDrillNum() {
        return drillNum;
    }

    public Date getConstructTime() {
        return constructTime;
    }

    public String getSteelBeltStart() {
        return steelBeltStart;
    }

    public String getSteelBeltEnd() {
        return steelBeltEnd;
    }

    public String getRemark() {
        return remark;
    }

    public String getRealDeep() {
        return realDeep;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public String getWorkfaceName() {
        return workfaceName;
    }

    public String getType() {
        return type;
    }
}
