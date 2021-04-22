package com.saturn.repositories;

import com.saturn.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);
}
