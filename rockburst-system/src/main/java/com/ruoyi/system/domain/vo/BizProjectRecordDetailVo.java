package com.ruoyi.system.domain.vo;

import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizVideo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BizProjectRecordDetailVo extends BizProjectRecord {


    @ApiModelProperty(value = "视频数据")
    private List<BizVideo> videoList;

    @ApiModelProperty(value = "钻孔组数据")
    private List<BizDrillRecord> drillRecordList;

    @ApiModelProperty(value = "施工地点")
    private String constructLocation;

    @ApiModelProperty(value = "最后审批人")
    private String auditLastName;

    private String DrillCoordinate;

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
