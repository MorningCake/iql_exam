package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO - Ответ успешной аутентификации
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Ответ успешной аутентификации", description = "JWT токен")
public class AuthResponse {

    /**
     * JWT токен
     */
    @Schema(description = "JWT токен")
    private String jwtToken;
}
