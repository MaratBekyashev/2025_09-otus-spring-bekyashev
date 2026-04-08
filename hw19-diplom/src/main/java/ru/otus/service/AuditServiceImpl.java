package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.entity.AuditLog;
import ru.otus.repository.AuditLogRepository;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditRepo;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String entityName,
                    Long entityId,
                    String action,
                    String userLogin) {
        var auditRow = new AuditLog();
        auditRow.setId(null);
        auditRow.setAction(action);
        auditRow.setEntityType(entityName);
        auditRow.setEntityId(entityId);
        auditRow.setCreateUser(userLogin);
        auditRow.setCreateDate(LocalDateTime.now());

        auditRepo.save(auditRow);
    }

}