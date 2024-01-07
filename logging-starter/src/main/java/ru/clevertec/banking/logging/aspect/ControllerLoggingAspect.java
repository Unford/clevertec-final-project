package ru.clevertec.banking.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class ControllerLoggingAspect extends MethodLoggingAspect {
    @Pointcut("execution(public * (@org.springframework.web.bind.annotation.RestController *).*(..))")
    public void restControllerMethods() {
    }


    @Pointcut("(@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void requestMappingMethods() {
    }


    @Pointcut("restControllerMethods() && requestMappingMethods()")
    public void controllerMethodAndRequestMappings() {
    }


    @Around("controllerMethodAndRequestMappings()")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.logMethod(joinPoint);
    }


}
