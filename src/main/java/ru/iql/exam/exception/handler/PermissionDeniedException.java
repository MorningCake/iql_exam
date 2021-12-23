package ru.iql.exam.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Permission Denied")
public class PermissionDeniedException extends IllegalArgumentException {

    public PermissionDeniedException(String msg) {
        super(msg);
    }
}
