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
public class BaseUserDto {

    /**
     * Имя
     */
    @NotBlank
    @Schema(description = "Имя", required = true)
    private String name;

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
}
