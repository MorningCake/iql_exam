package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для создания / редактирования
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Новый пользователь", description = "Данные для создания/редактирования пользователя")
public class NewUserDto {

    /**
     * Возраст
     */
    @NotNull @Positive
    @Schema(description = "Возраст", required = true, minimum = "1")
    private Integer age;

    /**
     * Эл.почта
     */
    @NotBlank @Email
    @Schema(description = "Эл.почта", required = true)
    private String email;

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
    private List<@Valid NewUserPhoneDto> phones = new ArrayList<>();
}
