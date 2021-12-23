package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для возврата на фронт
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Учетные данные пользователя", description = "Учетные данные созданного пользователя")
public class UserCredentialsDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор")
    private Long id;

    /**
     * Логин
     */
    @Schema(description = "Логин")
    private String login;
}
