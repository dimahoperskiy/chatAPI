package com.saturn.repositories;

import com.saturn.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс для работы с БД для UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Метод для поиска юзера по логину
     * @param login Логин
     * @return User
     */
    User findByLogin(String login);

    /**
     * Метод для проверки существования пользователя по логину
     * @param login Логин
     * @return Boolean
     */
    Boolean existsByLogin(String login);

    /**
     * Метод для проверки существования пользователя по почте
     * @param email Почта
     * @return Boolean
     */
    Boolean existsByEmail(String email);
}
