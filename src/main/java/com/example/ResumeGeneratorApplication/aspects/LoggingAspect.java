package com.example.ResumeGeneratorApplication.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.JoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Use reusable pointcut from ReusablePointcuts by referencing the static bean method name
    @Before("com.example.ResumeGeneratorApplication.aspects.ReusablePointcuts.serviceLayer()")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        log.info("[AOP][BEFORE] {}.{}() args={} ",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @After("com.example.ResumeGeneratorApplication.aspects.ReusablePointcuts.serviceLayer()")
    public void afterServiceMethod(JoinPoint joinPoint) {
        log.info("[AOP][AFTER] {}.{}() args={} ",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }


    @AfterReturning(pointcut = "com.example.ResumeGeneratorApplication.aspects.ReusablePointcuts.serviceOrAnnotated()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info("[AOP][AFTER_RETURNING] {}.{}() returned={}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "com.example.ResumeGeneratorApplication.aspects.ReusablePointcuts.serviceOrAnnotated()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.warn("[AOP][AFTER_THROWING] {}.{}() threw {}: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    /**
     * Around advice to measure execution time and optionally alter behavior.
     * Demonstrates use of an execution pointcut directly as well.
     */
    @Around("execution(* com.example.ResumeGeneratorApplication.service.ResumeService.*(..))")
    public Object aroundResumeServiceMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object ret = proceedingJoinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[AOP][AROUND] {} executed in {} ms", proceedingJoinPoint.getSignature(), elapsed);
            return ret;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[AOP][AROUND] {} threw {} after {} ms", proceedingJoinPoint.getSignature(), t.getClass().getSimpleName(), elapsed);
            throw t;
        }
    }
}
