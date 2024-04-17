package com.Management.TaskManagement.Helpers;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(value = "execution(* com.Management.TaskManagement.Controllers.*.*(..))", returning = "result")
    public void logControllerMethods(Object result) {
        logger.info("Controller method executed successfully with result: {}", result);
    }

    @AfterReturning(value = "execution(* com.Management.TaskManagement.Services.*.*(..))", returning = "result")
    public void logServiceMethods(Object result) {
        logger.info("Service method executed successfully with result: {}", result);
    }

    @AfterReturning(value = "execution(* com.Management.TaskManagement.Repositories.*.*(..))", returning = "result")
    public void logRepositoryMethods(Object result) {
        logger.info("Repository method executed successfully with result: {}", result);
    }
}