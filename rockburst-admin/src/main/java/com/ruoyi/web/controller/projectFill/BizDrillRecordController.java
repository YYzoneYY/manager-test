package com.ruoyi.web.controller.projectFill;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.common.core.domain.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.dto.BizDrillRecordDto;
import io.swagger.annotations.Api;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.service.IBizDrillRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 钻孔参数记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api("钻孔参数记录Controller")
@RestController
@RequestMapping("/drill/record")
public class BizDrillRecordController extends BaseController
{
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;

    /**
     * 查询钻孔参数记录列表
     */
    @PreAuthorize("@ss.hasPermi('drill:record:list')")
    @GetMapping("/list")
    public R<List<BizDrillRecord>> list(@ParameterObject BizDrillRecordDto dto , Pagination pagination)
    {
        QueryWrapper<BizDrillRecord> queryWrapper = new QueryWrapper<BizDrillRecord>();
        queryWrapper.lambda().eq(dto.getStatus() != null , BizDrillRecord::getStatus, dto.getStatus());
        List<BizDrillRecord> list = bizDrillRecordService.list(queryWrapper);
        return R.ok(list);
    }



}
