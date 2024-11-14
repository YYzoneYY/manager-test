package com.ruoyi.common.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BasePermission implements Serializable {

    private List<Long> deptIds;

    private Integer dateScopeSelf;
}
