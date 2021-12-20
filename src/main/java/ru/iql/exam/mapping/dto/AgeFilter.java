package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * DTO для поиска по фильтру
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Фильтр по возрасту", description = "Фильтр для поиска пользователя по возрасту")
public class AgeFilter extends BaseFilter {

    /**
     * Возраст
     */
    @NotNull @Positive
    @Schema(description = "Возраст", required = true, minimum = "1")
    private Integer age;
}
