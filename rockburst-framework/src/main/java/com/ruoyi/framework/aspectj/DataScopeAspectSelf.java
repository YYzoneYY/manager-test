package com.ruoyi.framework.aspectj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.context.PermissionContextHolder;
import com.ruoyi.system.domain.SysRoleDept;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleDeptMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspectSelf
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;


    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScopeSelf controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScopeSelf controllerDataScope)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext());
//                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), "system:user:list");
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(), controllerDataScope.userAlias(), permission,sysRoleDeptMapper,sysDeptMapper);
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias, String permission,SysRoleDeptMapper sysRoleDeptMapper,SysDeptMapper sysDeptMapper)
    {
        List<Long> deptIds = new ArrayList<>();
        Integer dataScopeSelf = 0;
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();
        List<Long> scopeCustomIds = new ArrayList<Long>();
        user.getRoles().forEach(role -> {
            if (DATA_SCOPE_CUSTOM.equals(role.getDataScope()) && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                scopeCustomIds.add(role.getRoleId());
            }
        });

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (conditions.contains(dataScope) || StringUtils.equals(role.getStatus(), UserConstants.ROLE_DISABLE))
            {
                continue;
            }

            if (DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                dataScopeSelf = 1;
                break;
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                QueryWrapper<SysRoleDept> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(SysRoleDept::getRoleId, scopeCustomIds);
                List<SysRoleDept> sysRoleDepts = sysRoleDeptMapper.selectList(queryWrapper);
                if(sysRoleDepts != null && sysRoleDepts.size() > 0){
                    deptIds.addAll(sysRoleDepts.stream()
                            .map(SysRoleDept::getDeptId)  // 提取 deptId
                            .collect(Collectors.toList()));
                }
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope))
            {
                deptIds.add(user.getDeptId());
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                List<SysDept> sysDepts = sysDeptMapper.selectChildrenDeptById(user.getDeptId());
                if(sysDepts != null && sysDepts.size() > 0){
                    deptIds.addAll(sysDepts.stream()
                            .map(SysDept::getDeptId)  // 提取 deptId
                            .collect(Collectors.toList()));
                }
                deptIds.add(user.getDeptId());
            }
            else if (DATA_SCOPE_SELF.equals(dataScope))
            {
                dataScopeSelf = 5;
            }
            conditions.add(dataScope);
        }


        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BasePermission)
        {
            BasePermission baseEntity = (BasePermission) params;
            baseEntity.setDeptIds(deptIds);
            baseEntity.setDateScopeSelf(dataScopeSelf);
//                baseEntity.getParams().put(DATA_SCOPE, "  (" + sqlString.substring(4) + ")");
        }

    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BasePermission) {
            BasePermission baseEntity = (BasePermission) params;
            baseEntity.setDeptIds(new ArrayList<>());
            baseEntity.setDateScopeSelf(0);
        }
    }
}
