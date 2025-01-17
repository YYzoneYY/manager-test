package com.ruoyi.framework.security.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import com.ruoyi.common.core.text.Convert;

/**
 * 权限信息
 * 
 * @author ruoyi
 */
public class PermissionContextHolder
{
    private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT";

    public static void setContext(String permission)
    {
        System.out.println("permission = " + permission);
        RequestContextHolder.currentRequestAttributes().setAttribute(PERMISSION_CONTEXT_ATTRIBUTES, permission,
                RequestAttributes.SCOPE_REQUEST);
    }

    public static String getContext()
    {
        RequestAttributes i = RequestContextHolder.currentRequestAttributes();
        System.out.println("i.getAttribute(PERMISSION_CONTEXT_ATTRIBUTES,\n                RequestAttributes.SCOPE_REQUEST) = " + i.getAttribute(PERMISSION_CONTEXT_ATTRIBUTES,
                RequestAttributes.SCOPE_REQUEST));
        return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute(PERMISSION_CONTEXT_ATTRIBUTES,
                RequestAttributes.SCOPE_REQUEST));
    }
}
