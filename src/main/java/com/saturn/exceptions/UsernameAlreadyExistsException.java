package com.saturn.exceptions;

/**
 * Ошибка - логин занят
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}