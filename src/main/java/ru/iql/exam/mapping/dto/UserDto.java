package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO для возврта на фронт
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Пользователь", description = "Данные созданного пользователя")
public class UserDto extends BaseUserDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор")
    private Long id;

    /**
     * Профиль
     */
    @Schema(description = "Профиль")
    private UserProfileDto profile;

    /**
     * Список номеров телефонов
     */
    @Schema(description = "Список номеров телефонов")
    @Builder.Default
    private Set<UserPhoneDto> phones = new HashSet<>();

    /**
     * Учетные данные
     */
    @Schema(description = "Учетные данные", required = true)
    private UserCredentialsDto credentials;
}
