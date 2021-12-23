package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * DTO - Запрос аутентификации
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Запрос аутентификации", description = "Учетные данные для аутентификации")
public class AuthRequest {

    /**
     * Логин
     */
    @NotBlank
    @Schema(description = "Логин", required = true)
    private String login;

    /**
     * Пароль
     */
    @NotBlank
    @Schema(description = "Пароль", required = true)
    private String password;
}
