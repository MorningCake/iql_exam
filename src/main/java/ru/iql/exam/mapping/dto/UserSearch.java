package ru.iql.exam.mapping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DTO для поиска
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(title = "Параметры поиска", description = "Фильтры и параметры пагинации для поиска пользователя")
public class UserSearch {

    /**
     * Фильтр по возрасту
     */
    @Valid
    @Schema(description = "Фильтр по возрасту")
    private AgeFilter ageFilter;

    /**
     * Фильтр по балансу
     */
    @Valid
    @Schema(description = "Фильтр по балансу")
    private CashFilter cashFilter;

    /**
     * Фильтр по имени (like)
     */
    @Schema(description = "Фильтр по имени (like)")
    private String name;

    /**
     * Фильтр по эл.почте (like)
     */
    @Schema(description = "Фильтр по эл.почте (like)")
    private String email;

    /**
     * Фильтр по номеру телефона (like)
     */
    @Schema(description = "Фильтр по номеру телефона (like)")
    private String phone;

    /**
     * Номер страницы, начиная с 0 (пагинация)
     */
    @Schema(description = "Номер страницы (пагинация)", required = true, minimum = "0")
    @NotNull @PositiveOrZero
    private Integer pageNumber;

    /**
     * Количество элементов на странице (пагинация)
     */
    @Schema(description = "Количество элементов на странице (пагинация)", required = true, minimum = "1")
    @NotNull @Positive
    private Integer pageSize;
}
