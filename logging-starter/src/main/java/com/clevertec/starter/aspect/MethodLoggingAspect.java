package com.clevertec.starter.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

@Slf4j
public abstract class MethodLoggingAspect {
    protected Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuilder builder = new StringBuilder()
                .append("Class: ")
                .append(joinPoint.getSignature().getDeclaringType().getSimpleName())
                .append(" - Method: ")
                .append(joinPoint.getSignature().getName());

        logArguments(joinPoint, builder);

        try {
            Object returnValue = joinPoint.proceed();
            builder.append(", returned: ").append(returnValue);
            log.info(builder.toString());
            return returnValue;

        } catch (Throwable e) {
            builder.append(", error: ").append(e.getClass())
                    .append(" ").append(e.getMessage());
            log.error(builder.toString());
            throw e;
        }
    }

    private void logArguments(ProceedingJoinPoint joinPoint, StringBuilder builder) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            builder.append(", args=[ ");
            Arrays.stream(args).forEach(arg -> builder.append(arg).append(" | "));
            builder.delete(builder.length() - 2, builder.length());
            builder.append("]");
        } else {
            builder.append(", no args");
        }
    }
}
