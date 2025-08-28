package com.ruoyi.web.controller.es;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.actual.ActualDTO;
import com.ruoyi.system.domain.dto.actual.WarnMessageDTO;
import com.ruoyi.system.service.MeasureActualService;
import com.ruoyi.system.service.WarnMessageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private TokenService tokenService;

    @PostMapping(value = "/createActualIndex")
    public R<Object> createActualIndex() {
        return R.ok(this.measureActualService.createIndex());
    }

    @PostMapping(value = "/createWarnIndex")
    public R<Object> createWarnIndex() {
        return R.ok(this.warnMessageService.createIndex());
    }

    @PostMapping(value = "/addWarnInfo")
    public R<Object> addWarnMessage(@RequestBody WarnMessageDTO warnMessageDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnMessageService.insertWarnMessage(warnMessageDTO, mineId));
    }

    @PutMapping(value = "/updateWarnInfo")
    public R<Object> updateWarnMessage(@RequestBody WarnMessageDTO warnMessageDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnMessageService.updateWarnMessage(warnMessageDTO, mineId));
    }

    @PostMapping(value = "/addActualInfo")
    public R<Object> addActualInfo(@RequestBody ActualDTO actualDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.measureActualService.insert(actualDTO, mineId));
    }
}