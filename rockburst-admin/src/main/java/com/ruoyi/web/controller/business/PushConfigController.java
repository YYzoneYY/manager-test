package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.PushConfigDTO;
import com.ruoyi.system.service.PushConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/7/2
 * @description:
 */

@Api(tags = "推送配置")
@RestController
@RequestMapping(value = "/pushConfig")
public class PushConfigController {

    @Resource
    private PushConfigService pushConfigService;

    @ApiOperation(value = "新增推送配置", notes = "新增推送配置")
    @PostMapping(value = "/add")
    public R<Object> addPushConfig(@RequestBody List<PushConfigDTO> pushConfigDTO) {
        return R.ok(this.pushConfigService.batchInsert(pushConfigDTO));
    }
}