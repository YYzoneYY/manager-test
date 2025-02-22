package com.ruoyi.system.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class JsonVo {
    private String label;
    private Long value;
    private List<JsonVo> child;
}
