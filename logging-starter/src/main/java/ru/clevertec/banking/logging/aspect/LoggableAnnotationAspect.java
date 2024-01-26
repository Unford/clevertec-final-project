package ru.clevertec.banking.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class LoggableAnnotationAspect extends MethodLoggingAspect {
    @Pointcut("@within(ru.clevertec.banking.logging.annotation.Loggable) ||" +
            " @annotation(ru.clevertec.banking.logging.annotation.Loggable)")
    public void loggableClassOrMethod() {
    }

    @Around("loggableClassOrMethod()")
    public Object logLoggableMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.logMethod(joinPoint);
    }


}
