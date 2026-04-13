package org.pwr.cloud.lab.common.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(org.pwr.cloud.lab..presentation..*) || " + "within(org.pwr.cloud.lab..application..*) || "
            + "within(org.pwr.cloud.lab..infrastructure..*Listener*)"
            + "within(org.pwr.cloud.lab..infrastructure..*Service*)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        var className = joinPoint.getSignature().getDeclaringTypeName();
        var methodName = joinPoint.getSignature().getName();
        var args = joinPoint.getArgs();

        log.info(">>> ENTER: {}.{}() with arguments = {}", className, methodName, Arrays.toString(args));
        try {
            var result = joinPoint.proceed();
            log.info("<<< EXIT: {}.{}() with result = {}", className, methodName, result);
            return result;
        } catch (Exception e) {
            log.error(
                    "!!! ERROR in {}.{}() with arguments = {}. Exception: {}",
                    className,
                    methodName,
                    Arrays.toString(args),
                    e.getMessage());
            throw e;
        }
    }
}
