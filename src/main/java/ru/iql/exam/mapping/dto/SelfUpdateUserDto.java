package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO для редактирования пользователем собственных данных
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(
        title = "Свой профиль для редактирования", description = "Данные для редактирования пользователем собственных данных")
public class SelfUpdateUserDto {

    /**
     * Эл.почта
     */
    @NotBlank
    @Email
    @Schema(description = "Эл.почта", required = true)
    private String email;
}
