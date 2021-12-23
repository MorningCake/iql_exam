package ru.iql.exam.exception.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Данные сущности.
 */
@Builder
@Getter
@Schema(title = "Сущность ошибки", description = "Сущность, спровоцировавшая ошибку")
public class Entity {
    
    @Schema(description = "Название сущности")
    private final String name;
    
    @Schema(description = "Идентифицирующее значение сущности")
    private final String id;
    
}
