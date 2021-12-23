package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * DTO для возврта на фронт
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Номер телефона", description = "Данные созданного номера телефона")
public class UserPhoneDto extends NewUserPhoneDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор", required = true)
    @NotNull
    private Long id;
}
