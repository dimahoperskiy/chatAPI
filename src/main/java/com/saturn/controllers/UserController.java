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


@CrossOrigin
@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @GetMapping("profile")
    public User findMe(@CookieValue("auth") String token) {
        String user =  jwtProvider.getLoginFromToken(token);
        return userRepository.findByLogin(user);
    }

    @GetMapping("/{login}")
    public User findByLogin(@PathVariable(name = "login") String login) {
        return userRepository.findByLogin(login);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping
    public void follow(@RequestBody Follow follow) {
        userRepository.findById(follow.getId()).map(user -> {
            user.setFollow(follow.getFollow());
            return userRepository.save(user);
        });
    }
}

