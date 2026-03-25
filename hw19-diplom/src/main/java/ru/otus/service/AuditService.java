package ru.otus.service;

public interface AuditService {

    void log(String entityName, Long entityId, String action, String userLogin);

}
