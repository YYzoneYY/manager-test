package com.ruoyi.system.domain.utils;


import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class CrumbWeight {
    String label;
    String value;
    Double value1;

    public void setValue1(Double value1) {
        this.value1 = value1;
    }

    public void setValue(String value) {
        this.value = value;
        this.value1 = Double.parseDouble(value);
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
