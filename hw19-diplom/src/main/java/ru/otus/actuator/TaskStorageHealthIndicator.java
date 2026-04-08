package ru.otus.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.repository.TaskRepository;

@Component
@RequiredArgsConstructor
public class TaskStorageHealthIndicator implements HealthIndicator {

    private final TaskRepository taskRepository;

    @Override
    public Health health() {
        try {
            long count = taskRepository.count();

            return Health.up()
                    .withDetail("tasks.currentCount", count)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Task storage is not available")
                    .build();
        }
    }
}
