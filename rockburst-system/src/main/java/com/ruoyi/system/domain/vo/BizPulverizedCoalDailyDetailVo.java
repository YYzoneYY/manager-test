package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.excel.BizProJson;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class BizPulverizedCoalDailyDetailVo {


    String riqi;

    String weizhi;

    String crumWeight;

    List<BizProJson> proJsons;




}
