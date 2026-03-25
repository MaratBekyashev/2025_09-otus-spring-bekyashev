package ru.otus.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.otus.annotation.Auditable;
import ru.otus.model.IdentifableEntity;
import ru.otus.service.AuditService;
import java.util.Collections;
import java.util.List;

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
        List<Long> entityIdList = extractIdentifiableEntityId(result);

        if (entityIdList == null) {
            String paramName = auditable.idFieldName();
            entityIdList = Collections.singletonList(extractIdFromMethodArgs(joinPoint, paramName));
        }

        var userDetails = (UserDetails) authentication.getPrincipal();
        String userLogin = userDetails.getUsername();
        for (Long entityId: entityIdList) {
            auditService.log(auditable.entity().name(), entityId, auditable.action().name(), userLogin);
        }
    }

    private List<Long> extractIdentifiableEntityId(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof IdentifableEntity enity) {
            Long id = enity.getId();
            return Collections.singletonList(id);
        }

        if (result instanceof List<?> list &&
            list.size() > 0 &&
            list.get(0) instanceof IdentifableEntity ) {
            List<Long> resultList = list.stream()
                    .map(e -> ((IdentifableEntity)e).getId())
                    .toList();
            return resultList;
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