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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        } else {
            throw new UsernameNotFoundException("User " + login + " not found");
//            throw new Exception("User " + login + " not found");
        }
        return null;
    }
}
