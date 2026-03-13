package ru.otus.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.annotation.Auditable;
import ru.otus.model.IdentifableEntity;
import ru.otus.security.CustomUserDetails;
import ru.otus.service.AuditService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        Long entityId = extractEntityId(result);

        if (entityId == null) {

        }

        Object[] args = joinPoint.getArgs();
        Long entityId = extractIdFromArgs(args);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userLogin = userDetails.getUser().getLogin();


        auditService.log(auditable.entity().name(), entityId, auditable.action().name(), userLogin);
    }

    private Long extractEntityId(Object result) {

        if (result == null) {
            return null;
        }

        try {
            if (result instanceof IdentifableEntity enity) {
                Long id = enity.getId();
                return id;
            }

            Method method = result.getClass().getMethod("id");
            Object id = method.invoke(result);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (NoSuchMethodException | IllegalAccessException| InvocationTargetException ignored) {
            var i=0;
        }

        return null;
    }

    private Long extractIdFromArgs(Object[] args) {

        for (Object arg : args) {

            if (arg instanceof Long) {
                return (Long) arg;
            }



        }

        return null;
    }
}