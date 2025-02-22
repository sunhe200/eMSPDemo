package com.example.emspdemo.component;

import cn.hutool.core.util.IdUtil;
import com.example.emspdemo.util.TraceHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * trace 日志切面
 *
 */
@Slf4j
@Aspect
@Component
public class TraceAspect {

    @Around("execution(* com.example.eMSPDemo.controller..*(..))")
    public Object traceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceHolder.set(IdUtil.fastSimpleUUID());
        try {
            return joinPoint.proceed();
        } finally {
            TraceHolder.remove();
        }
    }
}
