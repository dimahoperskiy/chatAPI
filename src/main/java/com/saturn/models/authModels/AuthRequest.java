package com.saturn.models.authModels;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
