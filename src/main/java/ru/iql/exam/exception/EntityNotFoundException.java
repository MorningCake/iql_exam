package ru.iql.exam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
public class EntityNotFoundException extends IllegalArgumentException {

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
