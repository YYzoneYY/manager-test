package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import com.ruoyi.system.domain.dto.ContentsTreeDTO;
import com.ruoyi.system.service.ContentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @ApiOperation(value = "获取所有文件夹树", notes = "获取所有文件夹树")
    @GetMapping(value = "/allContentsTree")
    public R<List<ContentsTreeDTO>> queryAllContents(){
        return R.ok(this.contentsService.queryAllContentsTree());
    }

    @ApiOperation(value = "获取文件夹树(包含自己)", notes = "获取文件夹树(包含自己)")
    @GetMapping(value = "/contentsTree")
    public R<List<ContentsTreeDTO>> getContentsTree(@ApiParam(name = "contentsId", value = "目录id", required = true) @RequestParam Long contentsId,
                                                      @ApiParam(name = "contentsName", value = "目录名称") @RequestParam(required = false) String contentsName){
        return R.ok(this.contentsService.getContentsTree(contentsId, contentsName));
    }
}