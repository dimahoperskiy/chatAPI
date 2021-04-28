package com.saturn.models.authModels;

import lombok.Data;

/**
 * Запрос на авторизацию
 */
@Data
public class AuthRequest {
    private String login;
    private String password;
}
