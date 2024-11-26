package com.ruoyi.system.domain.vo;

import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BizProjectRecordListVo extends BizProjectRecord {


    @ApiModelProperty(value = "施工地点")
    private String constructLocation;


    public String getConstructLocation() {
        if(BizBaseConstant.CONSTRUCT_TYPE_J.equals(super.getConstructType())){
            return super.getTunnelName();
        }
        return super.getWorkfaceName();
    }

    public void setConstructLocation(String constructLocation) {
        this.constructLocation = constructLocation;
    }
}
