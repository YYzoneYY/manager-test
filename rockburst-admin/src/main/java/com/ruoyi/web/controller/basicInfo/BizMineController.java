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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    @ApiOperation("查询全部矿井管理列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/alllist")
    public R<MPage<BizMine>> alllist(@ParameterObject BizMineDto dto, @ParameterObject Pagination pagination, HttpServletRequest request)
    {

        MPage<BizMine> list = bizMineService.selectBizMineList(dto,pagination);
        return R.ok(list);
//        if(getUsername().equals("admin")){

//        }

//        Long userId = getUserId();
//        SysUser user =  sysUserMapper.selectUserById(userId);
//        Long deptId = getDeptId();
//        SysDept dept = sysDeptService.selectDeptById(deptId);
//
//        String token = getToken(request);
//        Long mineId = tokenService.getMineIdFromToken(token);
//        if(mineId != null ){
//            dto.setMineId(mineId);
//        }
//
//
//        if(dept != null && dept.getCompanyId() != null){
//            SysCompany company = sysCompanyService.getById(dept.getCompanyId());
//            if(company != null &&  StringUtils.isNotEmpty(company.getCompanyName())){
//                List<Long> longList = Convert.toList(Long.class, StrUtil.split(company.getMineIds(), ','));
//                dto.setMineIds(longList);
//            }
//        }
//        MPage<BizMine> list = bizMineService.selectBizMineList(dto,pagination);
//        return R.ok(list);
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


    @ApiOperation("下拉矿列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizMine>> checkList(@ParameterObject BizMineDto mineDto,  @RequestParam(value = "状态合集", required = false) Long[] statuss)
    {

        if(getUsername().equals("admin")){
            List<Long> allMineIds = null;
            if(StrUtil.isNotEmpty(mineDto.getCompanyName())){
                QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().like(SysCompany::getCompanyName, mineDto.getCompanyName());
                List<SysCompany> companyList = sysCompanyService.list(queryWrapper);

                allMineIds = (companyList == null || companyList.isEmpty()) ?
                        Collections.emptyList() :
                        companyList.stream()
                                .filter(c -> c.getMineIds() != null && !c.getMineIds().trim().isEmpty()) // mineIds 判空
                                .flatMap(c -> Arrays.stream(c.getMineIds().split(",")))
                                .map(String::trim)                 // 去掉多余空格
                                .filter(s -> !s.isEmpty())         // 防止空字符串
                                .map(Long::valueOf)
                                .distinct()
                                .collect(Collectors.toList());
            }
            QueryWrapper<BizMine> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(StrUtil.isNotEmpty(mineDto.getMineName()), BizMine::getMineName,mineDto.getMineName());

            queryWrapper.lambda().in(allMineIds != null && allMineIds.size() > 0, BizMine::getMineId,allMineIds);
            List<BizMine> list = bizMineService.getBaseMapper().selectList(queryWrapper);
            return R.ok(list);
        }

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
        if(StrUtil.isNotEmpty(mineDto.getCompanyName())){
            QueryWrapper<SysCompany> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().like(SysCompany::getCompanyName, mineDto.getCompanyName());
            List<SysCompany> companyList = sysCompanyService.list(queryWrapper1);

            List<Long> allMineIds = (companyList == null || companyList.isEmpty()) ?
                    Collections.emptyList() :
                    companyList.stream()
                            .filter(c -> c.getMineIds() != null && !c.getMineIds().trim().isEmpty()) // mineIds 判空
                            .flatMap(c -> Arrays.stream(c.getMineIds().split(",")))
                            .map(String::trim)                 // 去掉多余空格
                            .filter(s -> !s.isEmpty())         // 防止空字符串
                            .map(Long::valueOf)
                            .distinct()
                            .collect(Collectors.toList());
            queryWrapper.lambda().in(allMineIds != null && allMineIds.size() > 0, BizMine::getMineId,allMineIds);

        }

        queryWrapper.lambda().like(StrUtil.isNotEmpty(mineDto.getMineName()), BizMine::getMineName,mineDto.getMineName());
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


}
