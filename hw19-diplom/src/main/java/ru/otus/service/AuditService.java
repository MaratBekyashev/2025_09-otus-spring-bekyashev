package ru.otus.service;

import ru.otus.entity.User;

public interface AuditService {

    void log(String entityName, Long entityId, String action, User user);
}
