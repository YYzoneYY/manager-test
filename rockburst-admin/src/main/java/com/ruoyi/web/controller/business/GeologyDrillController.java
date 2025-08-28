package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.ClassesSelectDTO;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;
import com.ruoyi.system.domain.vo.GeologyDrillVO;
import com.ruoyi.system.service.GeologyDrillService;
import com.ruoyi.system.service.MeasureActualService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private TokenService tokenService;


    @ApiOperation(value = "批量新增地质钻孔", notes = "批量新增地质钻孔")
    @PostMapping(value = "/batchInsert")
    public R<Object> batchInsert(@RequestBody List<GeologyDrillDTO> geologyDrillDTOS) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.geologyDrillService.batchInsert(geologyDrillDTOS, mineId));
    }

    @ApiOperation(value = "查询地质钻孔信息", notes = "查询地质钻孔信息")
    @GetMapping(value = "/obtainGeologyDrillInfo")
    public R<Object> obtainGeologyDrillInfo(@RequestParam("drillName") String drillName) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.geologyDrillService.obtainGeologyDrillInfo(drillName, mineId));
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/pageQueryList")
    public R<TableData> pageQueryList(@ApiParam(name = "drillName", value = "钻孔名称") @RequestParam(name = "drillName", required = false) String drillName,
                                      @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                      @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize){
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(geologyDrillService.pageQueryList(drillName, pageNum, pageSize, mineId));
    }

    @ApiOperation(value = "查询所有地质钻孔", notes = "查询所有地质钻孔")
    @GetMapping(value = "/obtainAllGeologyDrill")
    public R<List<GeologyDrillVO>> obtainGeologyDrillList(){
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(geologyDrillService.obtainGeologyDrillList(mineId));
    }

    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @GetMapping("/downloadTemplate")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws IOException {
        // 1. 加载模板文件资源
        ClassPathResource resource = new ClassPathResource("excel/地质钻孔关联信息导入模板.xlsx");

        // 2. 处理文件名编码问题（解决中文乱码）
        String fileName = "地质钻孔关联信息导入模板.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        // 3. 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 兼容各种浏览器的文件名编码方式
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFileName + "\"; " +
                        "filename*=UTF-8''" + encodedFileName);

        // 4. 设置正确的Content-Type
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(resource.contentLength());

        // 5. 构建响应实体
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @ApiOperation(value = "导入地质钻孔关联信息", notes = "导入地质钻孔关联信息")
    @PostMapping(value = "/importData/{geologyDrillId}")
    public R<Object> importData(@RequestPart("file") MultipartFile file, @PathVariable("geologyDrillId") Long geologyDrillId) {
        try {
            return R.ok(this.geologyDrillService.importData(file, geologyDrillId));
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "一键删除", notes = "一键删除")
    @DeleteMapping(value = "/oneClickDelete")
    public R<Object> oneClickDelete() {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.geologyDrillService.oneClickDelete(mineId));
    }
}