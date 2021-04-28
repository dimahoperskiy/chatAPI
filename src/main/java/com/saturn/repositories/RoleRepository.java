package com.saturn.repositories;

import com.saturn.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс для работы с БД для Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * @param name Название роли
     * @return Role
     */
    Role findByName(String name);
}
