package com.saturn.models.authModels;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Ответ при авторизации
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String login;
    private String email;
    private String status;
}