package com.saturn.services;

import com.saturn.exceptions.EmailAlreadyExistsException;
import com.saturn.exceptions.UsernameAlreadyExistsException;
import com.saturn.models.Role;
import com.saturn.models.User;
import com.saturn.repositories.RoleRepository;
import com.saturn.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис пользователя
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Метод для сохранения пользователя
     * Проверяет есть ли пользователь с таким логином и емейлом
     * @throws UsernameAlreadyExistsException Логин занят
     * @throws EmailAlreadyExistsException Емейл занят
     * @param user Пользовтель
     */
    public void saveUser(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new UsernameAlreadyExistsException("Exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Exists");
        }
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Метод для поиска пользователя по логину
     * @param login Логин
     * @return Пользователь
     */
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Метод для поиска пользователя по логину и паролю
     * Внутри с помощью PasswordEncoder проверяет на соотвествие введенного пароля
     * с захешированным паролем из БД
     * @param login Логин
     * @param password Пароль
     * @throws UsernameNotFoundException Пользователь не найден
     * @return Пользователь
     */
    public User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        } else {
            throw new UsernameNotFoundException("User " + login + " not found");
        }
        return null;
    }
}
