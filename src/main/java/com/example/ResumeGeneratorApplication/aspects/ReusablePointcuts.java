package com.example.ResumeGeneratorApplication.aspects;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class ReusablePointcuts {

    /**
     * execution pointcut for all methods in service package
     * Example: execution(* com.example.ResumeGeneratorApplication.service..*(..))
     */
    @Pointcut("execution(* com.example.ResumeGeneratorApplication.service..*(..))")
    public void serviceLayer() {}

    /**
     * within pointcut for controllers package
     */
    @Pointcut("within(com.example.ResumeGeneratorApplication.controller..*)")
    public void controllerLayer() {}

    /**
     * annotation pointcut matching methods annotated with @MyLogging
     */
    @Pointcut("@annotation(com.example.ResumeGeneratorApplication.aspects.MyLogging)")
    public void annotatedWithMyLogging() {}

    /**
     * combined reusable pointcut: service methods OR annotated methods
     */
    @Pointcut("serviceLayer() || annotatedWithMyLogging()")
    public void serviceOrAnnotated() {}

}
