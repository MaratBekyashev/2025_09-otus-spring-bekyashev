package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.entity.Role;
import ru.otus.model.RoleNameEnum;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role r where r.roleName in (:p_role_names)")
    List<Role> getAllByRoleNames(@Param("p_role_names") List<String> roleNames);
}
