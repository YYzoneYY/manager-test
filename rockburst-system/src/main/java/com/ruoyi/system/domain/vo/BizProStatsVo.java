package com.ruoyi.system.domain.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Accessors(chain = true)
public class BizProStatsVo {

    private Map<String,Object> locationMap;
    private Map<String,Object> typeMap;
    private Map<String,Object> unitMap;
}
