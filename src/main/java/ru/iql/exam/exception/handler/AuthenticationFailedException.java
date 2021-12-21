package ru.iql.exam.exception.handler;

import org.springframework.security.core.AuthenticationException;

/**
 * Ошибка аутентификации
 */
public class AuthenticationFailedException extends AuthenticationException {

    public AuthenticationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}
