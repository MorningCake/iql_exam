package ru.iql.exam.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка аутентификации
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Authentication Failed")
public class AuthenticationFailedException extends AuthenticationException {

    public AuthenticationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}
