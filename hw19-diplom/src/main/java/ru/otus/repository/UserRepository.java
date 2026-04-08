package ru.otus.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.otus.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph("user-with-roles-entity-graph")
    @Query("select u from User u")
    List<User> getAllUsers();

    @EntityGraph("user-with-roles-entity-graph")
    List<User> findAll(Specification<User> spec);

    @EntityGraph("user-with-roles-entity-graph")
    Optional<User> findByLogin(String login);

    boolean existsByLoginIgnoreCaseAndIsDeletedIsNull(String login);

}
