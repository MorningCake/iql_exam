package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * DTO для создания / редактирования
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Новый профиль", description = "Данные для создания/редактирования профиля")
public class NewUserProfileDto {

    /**
     * Состояние счета, коп.
     */
    @NotNull
    @Schema(description = "Состояние счета, коп.", required = true)
    private Integer cash;
}
