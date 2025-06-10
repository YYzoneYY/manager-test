package com.ruoyi.system.domain.dto.largeScreen;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/9
 * @description:
 */

@Data
public class SimpleTreeDTO {

    private String label;
    private Object value;
    private List<SimpleTreeDTO> children = new ArrayList<>();

    public SimpleTreeDTO() {}

    public SimpleTreeDTO(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public SimpleTreeDTO(String label, Object value, List<SimpleTreeDTO> children) {
        this.label = label;
        this.value = value;
        this.children = children == null ? new ArrayList<>() : children;
    }
}