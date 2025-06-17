package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;
import com.ruoyi.system.service.GeologyDrillService;
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
 * @date: 2025/6/17
 * @description:
 */
@Api(tags = "地质钻孔管理")
@RestController
@RequestMapping(value = "/geologyDrill")
public class GeologyDrillController {

    @Resource
    private GeologyDrillService geologyDrillService;


    @ApiOperation(value = "批量新增地质钻孔", notes = "批量新增地质钻孔")
    @PostMapping(value = "/batchInsert")
    public R<Object> batchInsert(@RequestBody List<GeologyDrillDTO> geologyDrillDTOS) {
        return R.ok(this.geologyDrillService.batchInsert(geologyDrillDTOS));
    }
}