package ru.iql.exam.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Тело исключения.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(title = "Ошибка", description = "Объект, описывающий ошибку")
public class ExceptionBody {
    
    @Schema(description = "Время возникновения ошибки")
    @Builder.Default
    private final ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.getId()));
    
    @Schema(description = "Вызываемый API")
    private final String path;
    
    @Schema(description = "Сущность, спровоцировавшая ошибку")
    private final Entity entity;
    
    @Schema(description = "Сообщение")
    private final String message;
    
    @Schema(description = "Проблемы, связанные с ошибкой")
    @Singular("problem")
    private final Set<Problem> problems;
    
}
