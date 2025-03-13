package com.ruoyi.system.domain.vo;


import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("巷道")
public class BizTunnelVo extends TunnelEntity {
    List<BizTravePoint> bizTravePoints;

    List<BizTunnelBar> bizTunnelBars;
}
