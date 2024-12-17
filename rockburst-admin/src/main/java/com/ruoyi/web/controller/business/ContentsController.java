package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import com.ruoyi.system.service.ContentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/17
 * @description:
 */

@Api(tags = "层级目录")
@RestController
@RequestMapping("/contents")
public class ContentsController {

    @Resource
    private ContentsService contentsService;

    @ApiOperation(value = "新增层级目录", notes = "新增层级目录")
    @PostMapping(value = "/add")
    public R<Object> addContents(@RequestBody ContentsEntity contentsEntity){
        return R.ok(this.contentsService.addContents(contentsEntity));
    }

    @ApiOperation(value = "修改层级目录", notes = "修改层级目录")
    @PutMapping(value = "/update")
    public R<Object> updateContents(@RequestBody ContentsEntity contentsEntity){
        return R.ok(this.contentsService.updateContents(contentsEntity));
    }

    @ApiOperation(value = "删除层级目录", notes = "删除层级目录")
    @DeleteMapping(value = "/delete")
    public R<Object> deleteContents(@ApiParam(name = "contentsIds", value = "层级id数组", required = true) @RequestParam Long[] contentsIds){
        return R.ok(this.contentsService.deleteById(contentsIds));
    }
}