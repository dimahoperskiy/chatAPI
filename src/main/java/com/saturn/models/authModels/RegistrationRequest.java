package com.saturn.models.authModels;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Запрос на регистрацию
 */
@Data
public class RegistrationRequest {
    @NotEmpty
    @Size(min = 2, max = 25, message = "Login must be between 2 and 25 characters long")
    private String login;
    @NotEmpty
    @Size(min = 2, message = "Email must be minimum 2 characters long")
    private String email;
    @NotEmpty
    @Size(min = 2, max = 25, message = "Password must be minimum 2 characters long")
    private String password;

}