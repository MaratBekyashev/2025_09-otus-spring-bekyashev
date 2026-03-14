package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.entity.AuditLog;
import ru.otus.repository.AuditLogRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public Page<AuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}