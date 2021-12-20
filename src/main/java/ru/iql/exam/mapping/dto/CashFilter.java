package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * DTO для поиска по фильтру
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Фильтр по балансу", description = "Фильтр для поиска пользователя по балансу на счете")
public class CashFilter extends BaseFilter {

    /**
     * Баланс
     */
    @NotNull
    @Schema(description = "Баланс", required = true)
    private Integer cash;
}
