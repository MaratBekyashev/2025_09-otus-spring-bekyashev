package ru.otus.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.repository.ProjectRepository;

@Component
@RequiredArgsConstructor
public class ProjectStorageHealthIndicator implements HealthIndicator {

    private final ProjectRepository projectRepository;

    @Override
    public Health health() {
        try {
            long count = projectRepository.count();

            return Health.up()
                    .withDetail("projects.currentCount", count)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Project storage is not available")
                    .build();
        }
    }
}
