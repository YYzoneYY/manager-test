package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.domain.Entity.SysCompany;
import com.ruoyi.system.domain.dto.BizMineDto;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.IBizMineService;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.system.service.ISysCompanyService;
import com.ruoyi.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 矿井管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-矿井管理")
//@Tag(description = "矿井管理Controller", name = "矿井管理Controller")
@RestController
@RequestMapping("/basicInfo/mine")
public class BizMineController extends BaseController
{
    @Autowired
    private IBizMineService bizMineService;

    @Autowired
    private IBizMiningAreaService   bizMiningAreaService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private ISysCompanyService sysCompanyService;
    @Autowired
    private SysUserMapper sysUserMapper;

    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }


    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询矿井管理列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/list")
    public R<MPage<BizMine>> list(@ParameterObject BizMineDto dto, @ParameterObject Pagination pagination, HttpServletRequest request)
    {

        Long userId = getUserId();
        SysUser user =  sysUserMapper.selectUserById(userId);
        Long deptId = getDeptId();
        SysDept dept = sysDeptService.selectDeptById(deptId);
//        if(dept != null && dept.getCompanyId() != null){
//
//
//            dto.setCompanyId(dept.getCompanyId());
//            MPage<BizMine> list = bizMineService.selectBizMineList(dto,pagination);
//            return R.ok(list);
//
//        }
        String token = getToken(request);
        Long mineId = tokenService.getMineIdFromToken(token);
        if(mineId != null ){
            dto.setMineId(mineId);
        }


        if(dept != null && dept.getCompanyId() != null){
            SysCompany company = sysCompanyService.getById(dept.getCompanyId());
            if(company != null &&  StringUtils.isNotEmpty(company.getCompanyName())){
                List<Long> longList = Convert.toList(Long.class, StrUtil.split(company.getMineIds(), ','));
                dto.setMineIds(longList);
            }
        }
        MPage<BizMine> list = bizMineService.selectBizMineList(dto,pagination);
        return R.ok(list);
    }


    @ApiOperation("下拉全部矿列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizMine>> checkList(@RequestParam(value = "状态合集", required = false) Long[] statuss)
    {

        BizMineDto dto = new BizMineDto();
        Long deptId = getDeptId();
        SysDept dept = sysDeptService.selectDeptById(deptId);
        if(dept != null && dept.getMineId() != null){
            dto.setMineId(dept.getMineId());
        }
        if(dept != null && dept.getCompanyId() != null){
            SysCompany company = sysCompanyService.getById(dept.getCompanyId());
            if(company != null &&  StringUtils.isNotEmpty(company.getCompanyName())){
                List<Long> longList = Convert.toList(Long.class, StrUtil.split(company.getMineIds(), ','));
                dto.setMineIds(longList);
            }
        }
        QueryWrapper<BizMine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .and(dto.getMineId() != null || (dto.getMineIds() != null && dto.getMineIds().size() > 0), i->i.eq(dto.getMineId() != null , BizMine::getMineId,dto.getMineId())
                        .or()
                        .in(dto.getMineIds() != null, BizMine::getMineId,dto.getMineIds())
                )
                .eq(BizMine::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizMine> list = bizMineService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }


    /**
     * 查询矿井管理列表
     */
//    @ApiOperation("查询矿井下拉列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
//    @GetMapping("/downList")
//    public R<MPage<BizMine>> downList()
//    {
//
//        List<BizMine> list = bizMineService.getBaseMapper().selectList(new QueryWrapper<BizMine>().orderByDesc("create_time"));
//        return R.ok(list);
//    }



    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取矿井管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
    @GetMapping(value = "/{mineId}")
    public R<BizMine> getInfo(@PathVariable("mineId") Long mineId)
    {
        return R.ok(bizMineService.selectBizMineByMineId(mineId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:add')")
    @Log(title = "矿井管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizMineDto dto)
    {
        BizMine entity = new BizMine();

        Long userId = getUserId();
        SysUser user =  sysUserMapper.selectUserById(userId);
        if(user != null && user.getCompanyId() != null) {
            dto.setCompanyId(user.getCompanyId());
        }
        BeanUtil.copyProperties(dto, entity);
        bizMineService.insertBizMine(entity);
        return R.ok();
    }
//    tokenService.createToken(loginUser);


    /**
     * 修改矿井管理
     */
    @ApiOperation("修改矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:edit')")
    @Log(title = "矿井管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizMineDto dto)
    {
        BizMine entity = new BizMine();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizMineService.updateBizMine(entity));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "矿井管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mineIds}")
    public R remove(@PathVariable Long[] mineIds)
    {
        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(BizMiningArea::getMineId, mineIds).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count == 0, "选择的矿井下还有采区");
        UpdateWrapper<BizMine> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizMine::getMineId, mineIds).set(BizMine::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizMineService.update(updateWrapper));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除矿井管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "矿井管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{mineId}")
    public R remove(@PathVariable("mineId") Long mineId)
    {
        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizMiningArea::getMineId, mineId).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count == 0, "选择的矿井下还有采区");
        BizMine entity = new BizMine();
        entity.setMineId(mineId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizMineService.updateBizMine(entity));
    }


    public static void main(String[] args) {
        try {
            // API URL
            URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("Authorization", "Bearer " + System.getenv("sk-9b6b5d723c7646f79808ff12e83c5260"));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON payload
            String jsonInputString = "{"
                    + "\"model\": \"deepseek-r1\","
                    + "\"messages\": ["
                    + "    {"
                    + "        \"role\": \"user\","
                    + "        \"content\": \"9.9和9.11谁大\""
                    + "    }"
                    + "]"
                    + "}";

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            try (java.io.InputStream is = connection.getInputStream();
                 java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A")) {
                String response = s.hasNext() ? s.next() : "";
                System.out.println("Response: " + response);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
