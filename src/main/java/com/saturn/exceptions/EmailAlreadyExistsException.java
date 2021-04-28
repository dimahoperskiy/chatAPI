package com.saturn.exceptions;

/**
 * Ошибка - Емейл занят
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}