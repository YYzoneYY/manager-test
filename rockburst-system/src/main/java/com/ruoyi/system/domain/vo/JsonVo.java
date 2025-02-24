package com.ruoyi.system.domain.vo;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@Setter
@Accessors(chain = true)
public class JsonVo {
    private String label;
    private Long value;
    private List<JsonChildVo> child;

    public String getLabel() {
        return label;
    }


    public Long getValue() {
        return value;
    }

    public List<JsonChildVo> getChild() {
        if (child == null) {
            return new ArrayList<>();
        }
        return child;
    }


}
