package com.ruoyi.aspectj;

import com.github.pagehelper.PageHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PageAspect {


    @Pointcut(value =
            "execution(* com.ruoyi.web.controller.basicInfo..*(..)) ||" +
                    " execution(* com.ruoyi.web.controller.eqt..*(..)) || " +
                    "execution(* com.ruoyi.web.controller.projectFill..*(..)) " )
    private void aspectPointcut() {

    }


    @Before("aspectPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        System.out.println("=================page参数处理===================");
        PageHelper.clearPage();
    }
}