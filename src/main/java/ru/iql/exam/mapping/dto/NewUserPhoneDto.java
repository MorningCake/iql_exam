package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * DTO для создания / редактирования
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Новый номер телефона", description = "Данные для создания/редактирования номера телефона")
public class NewUserPhoneDto {

    /**
     * Номер. Паттерн (пример) +74950123456
     */
    @NotBlank @Pattern(regexp = "^\\+7[\\d]{10}$")
    @Schema(description = "Номер телефона", required = true, pattern = "^\\+7[\\d]{10}$", example = "+74950123456")
    private String value;
}
