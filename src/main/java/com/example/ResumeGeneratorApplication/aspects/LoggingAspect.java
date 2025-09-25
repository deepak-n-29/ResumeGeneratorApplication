package com.example.ResumeGeneratorApplication.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.example.ResumeGeneratorApplication.service.ResumeServiceImpl.*(..))")
    public void beforeServiceMethods(JoinPoint joinPoint){
        log.info("Before method call: {}", joinPoint.getSignature());
    }

}
