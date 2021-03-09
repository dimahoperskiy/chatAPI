package com.saturn.controllers;

import com.saturn.models.User;
import com.saturn.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin
@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
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

class Follow {
    public Follow() {
    }

    public Follow(Long id, Boolean follow) {
        this.id = id;
        this.follow = follow;

    }

    private Long id;
    private Boolean follow;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }
}
