package ru.iql.exam.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Утилитный класс для работы с пагинацией
 */
public class PageUtils {

    public static Pageable getPageable(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
    }
}
