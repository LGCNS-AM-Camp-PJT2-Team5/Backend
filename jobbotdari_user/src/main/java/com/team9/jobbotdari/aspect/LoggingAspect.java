package com.team9.jobbotdari.aspect;

import com.team9.jobbotdari.entity.Log;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.repository.LogRepository;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Around("execution(* com.team9.jobbotdari.controller..*(..)) " +
            "|| execution(* com.team9.jobbotdari.service..*(..)) " +
            "|| (execution(* com.team9.jobbotdari.repository..*(..)) " +
            "    && !execution(* com.team9.jobbotdari.repository.LogRepository.*(..)))")
    public Object logAllLayers(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = methodSignature.getMethod().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        User currentUser = getCurrentUser();

        String enterAction = "ENTER: " + className + "." + methodName;
        String enterDescription = "EXECUTION: " + args;
        Log enterLog = Log.builder()
                .user(currentUser)
                .action(enterAction)
                .description(enterDescription)
                .build();
        logRepository.save(enterLog);
        log.info("[LOG] Action: {}", enterAction);
        log.info("[LOG] Description: {}", enterDescription);

        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        String exitAction = "EXIT: " + className + "." + methodName;
        String exitDescription = "Execution time: " + duration + "ms; Returned: " + result;
        Log exitLog = Log.builder()
                .user(currentUser)
                .action(exitAction)
                .description(exitDescription)
                .build();
        logRepository.save(exitLog);
        log.info("[LOG] Action: {}", exitAction);
        log.info("[LOG] Description: {}", exitDescription);

        return result;
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return ((CustomUserDetails) authentication.getPrincipal()).getUser();
            }
        } catch (Exception e) {
            log.warn("User 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
