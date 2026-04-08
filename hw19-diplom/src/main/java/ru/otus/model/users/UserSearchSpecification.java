package ru.otus.model.users;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.otus.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserSearchSpecification {

    public static Specification<User> build(UserSearchFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("isDeleted")));

            if (filter.login() != null) {
                predicates.add(cb.like(cb.lower(root.get("login")), "%" + filter.login().toLowerCase() + "%"));
            }
            if (filter.userName() != null) {
                predicates.add(cb.like(cb.lower(root.get("userName")), "%" + filter.userName().toLowerCase() + "%"));
            }
            if (filter.email() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.email().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}