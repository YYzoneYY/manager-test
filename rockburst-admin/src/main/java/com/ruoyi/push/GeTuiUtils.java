package com.ruoyi.push;

import cn.hutool.core.util.IdUtil;
import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.common.ApiResult;
import com.getui.push.v2.sdk.dto.req.Audience;
import com.getui.push.v2.sdk.dto.req.Settings;
import com.getui.push.v2.sdk.dto.req.Strategy;
import com.getui.push.v2.sdk.dto.req.message.PushBatchDTO;
import com.getui.push.v2.sdk.dto.req.message.PushChannel;
import com.getui.push.v2.sdk.dto.req.message.PushDTO;
import com.getui.push.v2.sdk.dto.req.message.PushMessage;
import com.getui.push.v2.sdk.dto.req.message.android.AndroidDTO;
import com.getui.push.v2.sdk.dto.req.message.android.GTNotification;
import com.getui.push.v2.sdk.dto.req.message.android.ThirdNotification;
import com.getui.push.v2.sdk.dto.req.message.android.Ups;
import com.getui.push.v2.sdk.dto.req.message.ios.Alert;
import com.getui.push.v2.sdk.dto.req.message.ios.Aps;
import com.getui.push.v2.sdk.dto.req.message.ios.IosDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeTuiUtils {

    @Resource(name = "myPushApi")
    private PushApi myPushApi;

    /**
     * 单点推送（离线不推送）
     *
     * @param cid     目标
     * @param title   标题
     * @param content 内容
     */
    public void pushMsg(String cid, String title, String content) {
        //根据cid进行单推
        PushDTO<Audience> pushDTO = new PushDTO<>();
        // 设置推送参数
        pushDTO.setRequestId(IdUtil.fastSimpleUUID());
        // 个推推送消息参数
        PushMessage pushMessage = new PushMessage();
        pushDTO.setPushMessage(pushMessage);
        GTNotification notification = new GTNotification();
        notification.setChannelLevel("3");
        pushMessage.setNotification(notification);
        notification.setTitle(title);
        notification.setBody(content);
        notification.setPayload("牛的");
        notification.setBigText("fgashkdashdkjashdjkashdjkashdasjkkkkkkkkkkkkkkkkkkkkkkk");//内容
        notification.setClickType("none");


        /** 带跳转url*/
//        GTNotification notification = new GTNotification();
//
//        notification.setTitle(title);
//        notification.setBody(content);
//        notification.setChannelLevel("4");
//
//        notification.setClickType("intent");
//        notification.setIntent("intent:#Intent;launchFlags=0x04000000;package=com.company.approve;component=pages%2Fplan%2Findex;S.gttask=;end");// 跳转地址
//        notification.setWant("{\"deviceId\".\"\", \"bundleName\":\"com.company.approve\", \"abilitylame\":\"TestAbility\", \"action\":\"com. test. action\",\"wi\": \"/pages/plan/index\",\"parameters\":{\"gttask\":\"\",\"name\":\"Getui\" \"age”:12}}");// 跳转地址
        pushMessage.setNotification(notification);
        /** 不带跳转url*/
//        pushMessage.setTransmission(content);
        pushDTO.setPushMessage(pushMessage);
        // 设置接收人信息
        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        audience.addCid(cid);
        // 进行cid单推
        ApiResult<Map<String, Map<String, String>>> apiResult = myPushApi.pushToSingleByCid(pushDTO);
        if (apiResult.isSuccess()) {
            // success
            System.out.println(apiResult.getData());
        } else {
            // failed
            System.out.println("code:" + apiResult.getCode() + ", msg: " + apiResult.getMsg());
        }
    }

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

    /**
     * 消息推送（离线推送）
     *
     * @param cid     目标
     * @param title   标题
     * @param content 内容
     */
    public void offlinePushMsg(String cid, String title, String content) {
        //根据cid进行单推
        PushDTO<Audience> pushDTO = new PushDTO<>();
        // 设置推送参数
        pushDTO.setRequestId(System.currentTimeMillis() + "");//requestid需要每次变化唯一
        //配置推送条件
        // 1: 表示该消息在用户在线时推送个推通道，用户离线时推送厂商通道;
        // 2: 表示该消息只通过厂商通道策略下发，不考虑用户是否在线;
        // 3: 表示该消息只通过个推通道下发，不考虑用户是否在线；
        // 4: 表示该消息优先从厂商通道下发，若消息内容在厂商通道代发失败后会从个推通道下发。
        Strategy strategy = new Strategy();
        strategy.setDef(1);
        Settings settings = new Settings();
        settings.setStrategy(strategy);
        pushDTO.setSettings(settings);
        settings.setTtl(3600000);//消息有效期，走厂商消息需要设置该值
        //推送苹果离线通知标题内容
        Alert alert = new Alert();
        alert.setTitle(title);//苹果离线通知栏标题
        alert.setBody(content);//苹果离线通知栏内容
        Aps aps = new Aps();
        //1表示静默推送(无通知栏消息)，静默推送时不需要填写其他参数。
        //苹果建议1小时最多推送3条静默消息
        aps.setContentAvailable(0);
        aps.setSound("default");
        aps.setAlert(alert);
        IosDTO iosDTO = new IosDTO();
        iosDTO.setAps(aps);
        iosDTO.setType("notify");
        PushChannel pushChannel = new PushChannel();
        pushChannel.setIos(iosDTO);
        //安卓离线厂商通道推送消息体
        PushChannel pushChannel1 = new PushChannel();
        AndroidDTO androidDTO = new AndroidDTO();
        Ups ups = new Ups();
        ThirdNotification notification1 = new ThirdNotification();
        ups.setNotification(notification1);
        notification1.setTitle(title);//安卓离线展示的标题
        notification1.setBody(content);//安卓离线展示的内容
        notification1.setClickType("intent");
        notification1.setIntent("intent:#Intent;launchFlags=0x04000000;action=android.intent.action.oppopush;component=io.dcloud.HBuilder/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=测试标题;S.content=测试内容;S.payload=test;end");
        //各厂商自有功能单项设置
        //ups.addOption("HW", "/message/android/notification/badge/class", "io.dcloud.PandoraEntry ");
        //ups.addOption("HW", "/message/android/notification/badge/add_num", 1);
        //ups.addOption("HW", "/message/android/notification/importance", "HIGH");
        //ups.addOption("VV","classification",1);
        androidDTO.setUps(ups);
        pushChannel1.setAndroid(androidDTO);
        pushDTO.setPushChannel(pushChannel1);

        // PushMessage在线走个推通道才会起作用的消息体
        PushMessage pushMessage = new PushMessage();
        pushDTO.setPushMessage(pushMessage);
        pushMessage.setTransmission(" {title:\"" + title + "\",content:\"" + content + "\",payload:\"自定义数据\"}");
        // 设置接收人信息
        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        audience.addCid(cid);// cid
        // 进行cid单推
        ApiResult<Map<String, Map<String, String>>> apiResult = myPushApi.pushToSingleByCid(pushDTO);
        if (apiResult.isSuccess()) {
            // success
            System.out.println(apiResult.getData());
        } else {
            // failed
            System.out.println("code:" + apiResult.getCode() + ", msg: " + apiResult.getMsg());
        }
    }

    /**
     * 官方api案例
     */
    public void offlinePushMsg1() {
        //根据cid进行单推
        PushDTO<Audience> pushDTO = new PushDTO<Audience>();
        // 设置推送参数
        pushDTO.setRequestId(System.currentTimeMillis() + "");
        /**** 设置个推通道参数 *****/
        PushMessage pushMessage = new PushMessage();
        pushDTO.setPushMessage(pushMessage);
        GTNotification notification = new GTNotification();
        pushMessage.setNotification(notification);
        notification.setTitle("个title");
        notification.setBody("个body");
        notification.setClickType("url");
        notification.setUrl("https://www.getui.com");
        /**** 设置个推通道参数，更多参数请查看文档或对象源码 *****/

        /**** 设置厂商相关参数 ****/
        PushChannel pushChannel = new PushChannel();
        pushDTO.setPushChannel(pushChannel);
        /*配置安卓厂商参数*/
        AndroidDTO androidDTO = new AndroidDTO();
        pushChannel.setAndroid(androidDTO);
        Ups ups = new Ups();
        androidDTO.setUps(ups);
        ThirdNotification thirdNotification = new ThirdNotification();
        ups.setNotification(thirdNotification);
        thirdNotification.setTitle("厂商title");
        thirdNotification.setBody("厂商body");
        thirdNotification.setClickType("url");
        thirdNotification.setUrl("https://www.getui.com");
        // 两条消息的notify_id相同，新的消息会覆盖老的消息，取值范围：0-2147483647
        // thirdNotification.setNotifyId("11177");
        /*配置安卓厂商参数结束，更多参数请查看文档或对象源码*/

        /*设置ios厂商参数*/
        IosDTO iosDTO = new IosDTO();
        pushChannel.setIos(iosDTO);
        // 相同的collapseId会覆盖之前的消息
        iosDTO.setApnsCollapseId("xxx");
        Aps aps = new Aps();
        iosDTO.setAps(aps);
        Alert alert = new Alert();
        aps.setAlert(alert);
        alert.setTitle("通知消息标题");
        alert.setBody("通知消息内容");
        /*设置ios厂商参数结束，更多参数请查看文档或对象源码*/

        /*设置接收人信息*/
        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        audience.addCid("xxx");
        /*设置接收人信息结束*/
        /**** 设置厂商相关参数，更多参数请查看文档或对象源码 ****/

        // 进行cid单推
        ApiResult<Map<String, Map<String, String>>> apiResult = myPushApi.pushToSingleByCid(pushDTO);
        if (apiResult.isSuccess()) {
            // success
            System.out.println(apiResult.getData());
        } else {
            // failed
            System.out.println("code:" + apiResult.getCode() + ", msg: " + apiResult.getMsg());
        }
    }
}

