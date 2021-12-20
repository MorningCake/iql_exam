package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.iql.exam.constant.ComparisonType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Базовый DTO для поиска по фильтру
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Базовый фильтр", description = "Базовый фильтр для поиска пользователя")
public abstract class BaseFilter {

    /**
     * Операция сравнения
     */
    @NotNull
    @Schema(description = "Знак сравнения", required = true,
            allowableValues = {"EQUAL", "LESS_THAN", "GREATER_THAN", "LESS_THAN_OR_EQUAL_TO", "GREATER_THAN_OR_EQUAL_TO"})
    private ComparisonType comparisonType;
}
