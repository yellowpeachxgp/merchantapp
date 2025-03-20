package com.example.merchantapp.aspect;

import com.example.merchantapp.model.OperationLog;
import com.example.merchantapp.model.User;
import com.example.merchantapp.repository.OperationLogRepository;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {
    @Autowired
    private OperationLogRepository operationLogRepository;

    @Around("execution(* com.example.merchantapp.controller..*(..)) && " +
            "!within(com.example.merchantapp.controller.AuthController) && " +
            "!within(com.example.merchantapp.controller.LogController)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前操作的用户名
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        User currentUser = (principal instanceof User ? (User) principal : null);
        String methodName = joinPoint.getSignature().toShortString();

        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            // 构造成功的操作日志
            OperationLog log = new OperationLog();
            log.setUser(currentUser);
            log.setAction(methodName);
            log.setDetails("成功调用接口");
            log.setTimestamp(LocalDateTime.now());
            operationLogRepository.save(log);
        } catch (Throwable ex) {
            // 构造失败的操作日志
            OperationLog log = new OperationLog();
            log.setUser(currentUser);
            log.setAction(methodName);
            log.setDetails("调用失败: " + ex.getMessage());
            log.setTimestamp(LocalDateTime.now());
            operationLogRepository.save(log);
            throw ex;  // 继续抛出异常
        }
        return result;
    }
}
