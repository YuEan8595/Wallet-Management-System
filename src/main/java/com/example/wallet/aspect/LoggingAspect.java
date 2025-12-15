package com.example.wallet.aspect;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut: apply to all service & controller methods
     */
    @Pointcut(
            "execution(* com.example.wallet.service..*(..)) || " +
                    "execution(* com.example.wallet.controller..*(..))"
    )
    public void applicationMethods() {}

    /**
     * Log method entry + parameters
     */
    @Before("applicationMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {

        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], parameterValues[i]);
        }

        log.info(
                "Entering {}.{}() with parameters = {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                signature.getName(),
                params
        );
    }

    /**
     * Log method exit + return value
     */
    @AfterReturning(
            pointcut = "applicationMethods()",
            returning = "result"
    )
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Exiting {}.{}() with result = {}",
                className,
                methodName,
                result
        );
    }

    /**
     * Log exceptions
     */
    @AfterThrowing(
            pointcut = "applicationMethods()",
            throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception in {}.{}(): {}",
                className,
                methodName,
                ex.getMessage()
        );
    }
}
