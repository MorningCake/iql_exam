package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для возврта на фронт
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Департамент", description = "Данные департамента")
public class DepartmentDto {

    /**
     *  Идентификатор
     */
    @Schema(description = "Идентификатор")
    private UUID id;

    /**
     * Наименование
     */
    @Schema(description = "Наименование")
    private String name;
}
