package ru.iql.exam.service;

import org.springframework.data.domain.Page;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;

/**
 * Сервис по работе с пользователями
 */
public interface UserService {
    /**
     * Поиск по фильтрам
     * @param params
     * @return
     */
    Page<User> searchWithFilters(UserSearch params);

    /**
     * Поиск по ID
     * @param id
     * @return
     */
    User findById(Long id);
}
