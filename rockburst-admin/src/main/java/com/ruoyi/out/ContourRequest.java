package com.ruoyi.out;

import java.util.List;

public class ContourRequest {
    private String file_name;
    private List<String> layer_names;

    // Getter 和 Setter
    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public List<String> getLayer_names() {
        return layer_names;
    }

    public void setLayer_names(List<String> layer_names) {
        this.layer_names = layer_names;
    }
}
