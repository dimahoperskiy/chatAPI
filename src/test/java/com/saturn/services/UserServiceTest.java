package com.saturn.services;

import com.saturn.models.User;
import com.saturn.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser() {
        String testLogin = "testUser1";

        User user = new User(testLogin, testLogin+"mail.ru", "1234");
        userService.saveUser(user);

        User userSaved = userRepository.findByLogin(testLogin);
        System.out.println(userSaved);

        assertEquals(user.getLogin(), userSaved.getLogin());

    }
}