package com.ruoyi.system.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AiModelStatus {
    private String taskId;
    private String status;
}
