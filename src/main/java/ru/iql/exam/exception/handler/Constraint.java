package ru.iql.exam.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Ограничение.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "Ограничение", description = "Поверяемое ограничение на поле")
public class Constraint {
    
    @Schema(description = "Тип ограничения")
    private final String type;
    
    @Schema(description = "Допустимые значения")
    private final Object value;
    
}
