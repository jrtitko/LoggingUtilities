package com.industriousgnomes.logging.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingInterceptor {

    @Around("(within(*))")
    public Object logIncomingAndOutgoing(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> clazz = AopUtils.getTargetClass(joinPoint.getTarget());
        Logger logger = LogManager.getLogger(clazz);
        
        Object response;
        if (logger.isTraceEnabled()) {
            String signature = joinPoint.getSignature().getName() + "(" + getArgList(joinPoint.getArgs()) + ")";
            
            logger.trace(signature + " - Start");
            response = joinPoint.proceed();
            logger.trace(signature + " - End");
        } else {
            response = joinPoint.proceed();
        }
            
        return response;
    }

    private String getArgList(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append((arg == null)?"NULL":arg.getClass().getSimpleName());
        }
        return sb.toString();
    }

    
}
