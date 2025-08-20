package com.ruoyi.web.controller.es;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.service.MeasureActualService;
import com.ruoyi.system.service.WarnMessageService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */

@RestController
@RequestMapping("/esInit")
public class ESInitController {

    @Resource
    private MeasureActualService measureActualService;

    @Resource
    private WarnMessageService warnMessageService;

    @PostMapping(value = "/createActualIndex")
    public R<Object> createActualIndex() {
        return R.ok(this.measureActualService.createIndex());
    }

    @PostMapping(value = "/createWarnIndex")
    public R<Object> createWarnIndex() {
        return R.ok(this.warnMessageService.createIndex());
    }
}