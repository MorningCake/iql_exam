package ru.iql.exam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка аутентификации
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Authentication Failed")
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}
