package com.ruoyi.common.message.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: shikai
 * @date: 2025/6/12
 * @description:
 */

@Data
public class WebSocketMessage {

    private String message;

    @NotBlank(message = "用户不能为空！")
    private String touserid;
}