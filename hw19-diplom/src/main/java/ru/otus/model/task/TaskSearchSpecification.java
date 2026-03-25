package ru.otus.model.task;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.otus.entity.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskSearchSpecification {

    public static Specification<Task> build(TaskSearchFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.projectId() != null) {
                predicates.add(cb.equal(root.get("project").get("projectId"), filter.projectId()));
            }

            if (filter.assigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("userId"), filter.assigneeId()));
            }

            if (filter.statuses() != null && !filter.statuses().isEmpty()) {
                predicates.add(root.get("status").in(filter.statuses()));
            }

            if (filter.priority() != null) {
                predicates.add(cb.equal(root.get("priority"), filter.priority()));
            }

            if (filter.title() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.title().toLowerCase() + "%"));
            }

            if (filter.dueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), filter.dueDateFrom()));
            }
            if (filter.dueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), filter.dueDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}