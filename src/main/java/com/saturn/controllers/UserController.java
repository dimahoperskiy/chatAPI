package com.saturn.controllers;

import antlr.collections.List;
import com.saturn.configuration.jwt.JwtProvider;
import com.saturn.models.User;
import com.saturn.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер пользователя
 */
@CrossOrigin
@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserRepository userRepository;

    /**
     * Найти всех пользователей
     * @param pageable Пользователи
     * @return Пользователи (постраничный вывод)
     */
    @GetMapping
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Свой профиль
     * @param token Куки с авторизацией
     * @return Пользователь (свой)
     */
    @GetMapping("profile")
    public User findMe(@CookieValue("auth") String token) {
        String user =  jwtProvider.getLoginFromToken(token);
        return userRepository.findByLogin(user);
    }

    /**
     * Вернуть пользователя по логину
     * @param login Логин
     * @return Пользователь
     */
    @GetMapping("/{login}")
    public User findByLogin(@PathVariable(name = "login") String login) {
        return userRepository.findByLogin(login);
    }
    /**
     * @deprecated
     */
    @PutMapping
    public void follow(@RequestBody Follow follow) {
        userRepository.findById(follow.getId()).map(user -> {
            user.setFollow(follow.getFollow());
            return userRepository.save(user);
        });
    }
}

