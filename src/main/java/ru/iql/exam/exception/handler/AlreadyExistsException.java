package ru.iql.exam.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - параметр уже занят
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Already exists")
public class AlreadyExistsException extends IllegalArgumentException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
