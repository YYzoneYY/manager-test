package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.BizMine;
import com.github.yulichang.annotation.EntityMapping;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BizProjectRecordListVo extends BizProjectRecord {


    @ApiModelProperty(value = "施工地点")
    private String constructLocation;


    public String getConstructLocation() {
        if("huicai".equals(super.getConstructType())){
            return super.getTunnelName();
        }
        return super.getWorkfaceName();
    }

    public void setConstructLocation(String constructLocation) {
        this.constructLocation = constructLocation;
    }
}
