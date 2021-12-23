package ru.iql.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.iql.exam.mapping.dto.AuthRequest;
import ru.iql.exam.mapping.dto.AuthResponse;

import javax.validation.Valid;

/**
 * Контроллер аутентификации
 */
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Контроллер аутентификации")
public interface AuthController {

    /**
     * Аутентификация и получение токена
     * @param request AuthRequest
     * @return AuthResponse
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Аутентификация", description = "Аутентификация и получение токена")
    @ResponseBody
    AuthResponse auth(@RequestBody @Valid AuthRequest request);
}
