package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.service.DataCollectionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */

@RestController
@RequestMapping("/dataCollection")
public class DataCollectionController {

    @Resource
    private DataCollectionService dataCollectionService;

    @PostMapping("/save")
    public R<Object> dataCollectionSave(@RequestBody Map<String, Object> map) {
        return R.ok(this.dataCollectionService.dataCollectionSave( map));
    }
}