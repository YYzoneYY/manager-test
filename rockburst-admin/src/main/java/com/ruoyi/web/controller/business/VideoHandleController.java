package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.service.VideoHandleService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/3/7
 * @description:
 */

@Api(tags = "视频视频相关")
@RestController
@RequestMapping("/videoIdentify")
public class VideoHandleController {

    @Resource
    private VideoHandleService videoHandleService;

    @GetMapping("/saveUrl")
    public R<Object> saveUrl(@RequestParam String beforeVideoUrl, @RequestParam String afterVideoUrl) {
        return R.ok(videoHandleService.insertT(beforeVideoUrl, afterVideoUrl));
    }
}