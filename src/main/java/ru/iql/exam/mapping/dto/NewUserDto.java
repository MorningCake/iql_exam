package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для создания / редактирования
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Новый пользователь", description = "Данные для создания/редактирования пользователя")
public class NewUserDto extends BaseUserDto {

    /**
     * Профиль
     */
    @NotNull @Valid
    @Schema(description = "Профиль", required = true)
    private NewUserProfileDto profile;

    /**
     * Список номеров телефонов
     */
    @NotNull
    @Schema(description = "Список номеров телефонов", required = true)
    @Builder.Default
    private Set<@Valid NewUserPhoneDto> phones = new HashSet<>();

    /**
     * Учетные данные
     */
    @NotNull @Valid
    @Schema(description = "Учетные данные", required = true)
    private NewCredentialsDto credentials;

    /**
     * ID департамента
     */
    @NotNull
    @Schema(description = "ID департамента", required = true)
    private UUID departmentId;
}
