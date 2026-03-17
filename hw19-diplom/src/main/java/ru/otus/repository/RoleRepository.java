package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.entity.Role;
import ru.otus.model.RoleNameEnum;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleNameEnum roleName);

}
