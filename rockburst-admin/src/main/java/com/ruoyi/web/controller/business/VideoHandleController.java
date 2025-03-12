package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.service.VideoHandleService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Api(tags = "AI视频识别")
@RestController
@RequestMapping(value = "/videoIdentify")
public class VideoHandleController {

    @Resource
    private VideoHandleService videoHandleService;

    @ApiOperation(value = "保存识别后的视频(适用于视频识别)", notes = "保存识别后的视频(适用于视频识别)")
    @GetMapping("/saveUrl")
    public R<Object> saveUrl(@RequestParam String beforeVideoUrl, @RequestParam String afterVideoUrl) {
        return R.ok(videoHandleService.insertT(beforeVideoUrl, afterVideoUrl));
    }
}