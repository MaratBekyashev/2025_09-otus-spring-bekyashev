package ru.otus.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.entity.AuditLog;
import ru.otus.entity.User;
import ru.otus.repository.AuditLogRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditRepo;

    @Override
    @Transactional
    public void log(String entityName,
                    Long entityId,
                    String action,
                    User user) {
        var auditRow = new AuditLog();
        auditRow.setId(null);
        auditRow.setAction(action);
        auditRow.setEntityType(entityName);
        auditRow.setEntityId(entityId);
        auditRow.setUser(user);

        auditRepo.save(auditRow);
    }
    
}
