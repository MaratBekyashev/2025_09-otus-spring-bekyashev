package ru.otus.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.annotation.Auditable;
import ru.otus.model.IdentifableEntity;
import ru.otus.security.CustomUserDetails;
import ru.otus.service.AuditService;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void audit(JoinPoint joinPoint,
                      Auditable auditable,
                      Object result) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return;
        }
        Long entityId = extractIdentifiableEntityId(result);

        if (entityId == null) {
            String paramName = auditable.idFieldName();
            entityId = extractIdFromMethodArgs(joinPoint, paramName);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userLogin = userDetails.getUser().getLogin();

        auditService.log(auditable.entity().name(), entityId, auditable.action().name(), userLogin);
    }

    private Long extractIdentifiableEntityId(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof IdentifableEntity enity) {
            Long id = enity.getId();
            return id;
        }
        return null;
    }

    private Long extractIdFromMethodArgs(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (var i = 0; i < paramNames.length; i++){
            if (paramName.equals(paramNames[i])) {
                if (args[i] instanceof Long paramValue) {
                    return  paramValue;
                }
            }
        }
        return null;
    }
}