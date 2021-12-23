package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO для редактирования кред
 */
@Deprecated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Учетные данные пользователя для редактирования", description = "Учетные данные редактируемого пользователя")
public class UpdateCredentialsDto extends NewCredentialsDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор")
    private Long id;
}
