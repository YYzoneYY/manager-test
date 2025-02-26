package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.dto.PressureHoleImportDTO;
import com.ruoyi.system.service.PressureHoleFormsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/12
 * @description:
 */

@Api(tags = "卸压孔数据报表")
@RestController
@RequestMapping("/pressureHoleForms")
public class PressureHoleFormsController {

    @Resource
    private PressureHoleFormsService pressureHoleFormsService;

    @ApiOperation(value = "导出卸压孔报表", notes = "导出卸压孔报表")
    @GetMapping("/exportForms")
    public void exportPressureHoleForms(HttpServletResponse response,
                                        @RequestParam(value = "startTime") Long startTime,
                                        @RequestParam(value = "endTime") Long endTime) throws UnsupportedEncodingException {
        List<PressureHoleImportDTO> list = pressureHoleFormsService.ExportPressureHoleForms(new java.util.Date(startTime), new java.util.Date(endTime));
        ExcelUtil<PressureHoleImportDTO> excelUtil = new ExcelUtil<>(PressureHoleImportDTO.class);
        String fileName = "卸压孔报表" + ".xlsx";
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.reset();
        response.setHeader("Content-disposition", "attachment;filename="+fileName+";"+"filename*=utf-8''"+fileName);
        excelUtil.exportExcel(response, list, "卸压孔数据报表", "卸压孔施工台账");
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/queryPage")
    public R<Object> queryPage(@RequestParam Long startTime,
                               @RequestParam Long endTime,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        return R.ok(pressureHoleFormsService.queryPage(new Date(startTime), new Date(endTime), pageNum, pageSize));
    }

}