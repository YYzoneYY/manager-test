package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.service.ConstructDocumentService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */

@Api(tags = "文档管理")
@RestController
@RequestMapping(value = "/constructDocument")
public class ConstructDocumentController {

    @Resource
    private ConstructDocumentService constructDocumentService;

    @ApiOperation(value = "新增层级")
    @PostMapping(value = "/addLevel")
    public R<Object> addLevel(@RequestBody LevelDTO levelDTO) {
        return R.ok(this.constructDocumentService.insertLevel(levelDTO));
    }

    @ApiOperation(value = "修改层级")
    @PutMapping(value = "/updateLevel")
    public R<Object> updateLevel(@RequestBody LevelDTO levelDTO) {
        return R.ok(this.constructDocumentService.updateLevel(levelDTO));
    }

    @ApiOperation(value = "施工文件上传", notes = "施工文件上传")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "bucketName", value = "桶名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "dataId", value = "层级id", required = true, dataType = "Long")
    })
    @PostMapping("/addFile")
    public R<Object> addFile(@RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "bucketName", required = false) String bucketName,
                             @RequestParam(value = "dataId", required = false) Long dataId) {
        return R.ok(this.constructDocumentService.addFile(file, bucketName, dataId));
    }

    @ApiOperation(value = "施工文件修改", notes = "施工文件修改")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "bucketName", value = "桶名称", required = false, dataType = "String"),
    })
    @PutMapping("/updateFile")
    public R<Object> updateFile(@ApiParam(name = "file", value = "文件", required = true) @RequestParam MultipartFile file,
                                @RequestParam(value = "bucketName", required = false) String bucketName,
                                @RequestParam(value = "dataId") Long dataId,
                                @RequestParam(value = "fileIds") Long[] fileIds,
                                @RequestParam(value = "documentName", required = false) String documentName) {
        return R.ok(this.constructDocumentService.updateFile(file, bucketName, dataId, fileIds, documentName));
    }


    @PostMapping(value = "/queryByPage")
    @ApiOperation(value = "根据条件分页查询", notes = "根据条件分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = false, dataType = "Integer")
    })
    public R<TableData> queryByPage(@RequestBody(required = false) SelectDocumentDTO selectDocumentDTO,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        return R.ok(this.constructDocumentService.queryByPage(selectDocumentDTO, pageSize, pageNum));
    }

    /**
     * 获取上级区域名称下拉列表
     *
     * @return 返回结果
     */
    @ApiOperation(value = "获取上级层级下拉列表", notes = "获取上级层级下拉列表")
    @GetMapping("/DropDownList")
    public R<List<DropDownListDTO>> getDropDownList() {
        return R.ok(this.constructDocumentService.getDropDownList());
    }


    @ApiOperation(value = "上移/下移")
    @PostMapping(value = "/adjustOrder")
    public R<Object> moveOrder(@RequestBody AdjustOrderDTO adjustOrderDTO) {
        return R.ok(this.constructDocumentService.moveOrder(adjustOrderDTO));
    }

    @ApiOperation(value = "删除数据", notes = "删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "数据id", required = true, dataType = "Long")
    })
    @DeleteMapping("/deleteByDataId")
    public R<Object> deleteByDataId(@RequestParam(value = "dataId") Long dataId) {
        return R.ok(this.constructDocumentService.deleteByDataId(dataId));
    }

    @ApiOperation(value = "获取文件URL", notes = "获取文件URL")
    @GetMapping("/getFileUrl")
    public R<Object> downloadFile(@ApiParam(name = "fileId", value = "文件id", required = true) @RequestParam Long fileId) {
        return R.ok(this.constructDocumentService.getFileUrl(fileId));
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "数据id", required = true, dataType = "Long")
    })
    @GetMapping("/detail")
    public R<DocumentDTO> detail(@ApiParam(name = "dataId", value = "数据id",required = true) @RequestParam Long dataId) {
        return R.ok(this.constructDocumentService.detail(dataId));
    }
}