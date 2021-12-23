package ru.iql.exam.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Проблема.
 */
@Builder
@Getter
@JsonInclude
@EqualsAndHashCode(exclude = {"constraints"})
@Schema(title = "Проблема", description = "ПОписание проблемы, спровоцировавшей ошибку")
public class Problem {
    
    @Schema(description = "Название проблемного поля")
    private final String field;
    
    @Schema(description = "Проблемное значение")
    private final String value;
    
    @Schema(description = "Список ограничений, проверяемый сервером")
    @Builder.Default
    private final List<Constraint> constraints = new ArrayList<>();
    
}
