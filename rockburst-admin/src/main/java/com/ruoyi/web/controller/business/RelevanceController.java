package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.RelevanceDTO;
import com.ruoyi.system.service.RelevanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/9/4
 * @description:
 */

@Api(tags = "测点-工作面关联")
@RestController
@RequestMapping(value = "/relevance")
public class RelevanceController {

    @Resource
    private RelevanceService relevanceService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "关联工作面", notes = "关联工作面")
    @PostMapping(value = "/relevanceWorkFace")
    public R<Object> relevance(@ApiParam(name = "sensorType", value = "传感器类型", required = true) @RequestParam String sensorType,
                               @ApiParam(name = "relevanceDTO", value = "关联信息", required = true) @RequestBody RelevanceDTO relevanceDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.relevanceService.addRelevance(sensorType, relevanceDTO, mineId));
    }
}