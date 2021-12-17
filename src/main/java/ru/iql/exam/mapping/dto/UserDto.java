package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для возврта на фронт
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Пользователь", description = "Данные созданного пользователя")
public class UserDto extends NewUserDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор")
    private Long id;
}
