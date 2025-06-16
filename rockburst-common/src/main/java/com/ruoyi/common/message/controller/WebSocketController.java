package com.ruoyi.common.message.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.message.WebSocketServer;
import com.ruoyi.common.message.domain.WebSocketMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/webSocket")
@RequiredArgsConstructor
@Api(tags = "WebSocket推送")
public class WebSocketController {

    @ApiOperation("推送消息")
    @PostMapping("/pushMessage")
    public R<String> pushMessage(@RequestBody WebSocketMessage webSocketMessage){
        try {
            if("all".equals(webSocketMessage.getTouserid())){
                WebSocketServer.sendInfoAll(webSocketMessage.getMessage());
            } else {
                WebSocketServer.sendInfo(webSocketMessage.getMessage(),webSocketMessage.getTouserid());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
