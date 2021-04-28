package com.saturn.controllers;

import com.saturn.configuration.jwt.JwtProvider;
import com.saturn.exceptions.EmailAlreadyExistsException;
import com.saturn.exceptions.UsernameAlreadyExistsException;
import com.saturn.models.User;
import com.saturn.models.authModels.AuthRequest;
import com.saturn.models.authModels.AuthResponse;
import com.saturn.models.authModels.RegistrationRequest;
import com.saturn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Контроллер для авторизации
 */
@CrossOrigin
@RestController
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    /**
     * Регистрация
     * Создается токен в куки
     * @param registrationRequest Запрос
     * @param response Ответ
     * @return Пользователь зарегестрирован
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest,
                               HttpServletResponse response) {
        User u = new User();
        u.setPassword(registrationRequest.getPassword());
        u.setLogin(registrationRequest.getLogin());
        u.setEmail(registrationRequest.getEmail());
        try {
            userService.saveUser(u);
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Username exists");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Email exists");
        }

        String token = jwtProvider.generateToken(u.getLogin());
        Cookie cookie = new Cookie("auth", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().body("User registered");
    }

    /**
     * Авторизация
     * Создается токен в куки
     * @param request Запрос
     * @param response Ответ
     * @return Данные запроса
     */
    @PostMapping("/login")
    public ResponseEntity<?> auth(@RequestBody AuthRequest request, HttpServletResponse response) {
        try {
            User user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
            if (user != null) {
                String token = jwtProvider.generateToken(user.getLogin());
                Cookie cookie = new Cookie("auth", token);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(Integer.MAX_VALUE);
                cookie.setPath("/");
                response.addCookie(cookie);
                return ResponseEntity.ok().body(new AuthResponse(user.getId(),
                        user.getLogin(), user.getEmail(), user.getStatus()));
            } else {
                return ResponseEntity.badRequest().body("Incorrect password");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Выход
     * Удаляется куки
     * @param response Ответ
     * @return Вы вышли
     */
    @GetMapping("/exit")
    public ResponseEntity<String> exit(HttpServletResponse response) {
        Cookie deleteServletCookie = new Cookie("auth", null);
        deleteServletCookie.setMaxAge(0);
        response.addCookie(deleteServletCookie);
        return ResponseEntity.ok().body("Logged out!");
    }
}

