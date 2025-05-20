package com.ruoyi.common.warnPush;

import cn.hutool.core.util.IdUtil;
import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.common.ApiResult;
import com.getui.push.v2.sdk.dto.req.Audience;
import com.getui.push.v2.sdk.dto.req.message.PushBatchDTO;
import com.getui.push.v2.sdk.dto.req.message.PushDTO;
import com.getui.push.v2.sdk.dto.req.message.PushMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/5/20
 * @description:
 */

@Component
public class WarnPush {

    @Resource(name = "myPushApi")
    private PushApi myPushApi;


    /**
     * 推送给多个
     *
     * @param cids    目标集
     * @param title   标题
     * @param content 内容
     */
    public void pushMsg(List<String> cids, String title, String content) {
        List<PushDTO<Audience>> list = new ArrayList<>(cids.size());
        cids.forEach(cid -> {
            PushDTO<Audience> pushDTO = new PushDTO<>();
            // 唯一标识
            pushDTO.setRequestId(IdUtil.fastSimpleUUID());
            // 消息内容
            PushMessage pushMessage = new PushMessage();
            pushMessage.setNetworkType(0);
            // 透传消息内容
            pushMessage.setTransmission(" {title:\"" + title + "\",content:\"" + content + "\",payload:\"自定义数据\"}");
            pushDTO.setPushMessage(pushMessage);
            // 消息接受人
            Audience audience = new Audience();
            audience.addCid(cid);
            pushDTO.setAudience(audience);
            list.add(pushDTO);
        });
        PushBatchDTO pushBatchDTO = new PushBatchDTO();
        pushBatchDTO.setAsync(true);
        pushBatchDTO.setMsgList(list);
        ApiResult<Map<String, Map<String, String>>> mapApiResult = myPushApi.pushBatchByCid(pushBatchDTO);
        if (mapApiResult.isSuccess()) {
            // success
            System.out.println(mapApiResult.getData());
        } else {
            // failed
            System.out.println("code:" + mapApiResult.getCode() + ", msg: " + mapApiResult.getMsg());
        }
    }
}