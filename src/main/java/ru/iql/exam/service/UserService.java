package ru.iql.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

/**
 * Сервис по работе с пользователями
 */
public interface UserService {
    /**
     * Поиск по фильтрам
     * @param params параметры
     * @return Page<User>
     */
    Page<User> searchWithFilters(UserSearch params);

    /**
     * Поиск по email
     * @param email email
     * @return User
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск по ID
     * @param id ID
     * @return User
     */
    User findById(Long id) throws EntityNotFoundException;

    /**
     * Сохранить юзера, проверив уникальные поля
     * @param user User
     * @return User
     */
    User save(User user);

    /**
     * Обновить данные юзера, проверив уникальные поля
     * @param user новые данные
     * @param userFromDb сущность из БД
     * @return User
     */
    User update(User user, User userFromDb);

    /**
     * Обновить свои данные
     * @param user новые данные
     * @param userFromDb сущность из БД
     */
    void updateSelf(User user, User userFromDb);

    /**
     * Проверить, совпадает ли обновляемый пользователь с аутентификацией
     * @param updatedUser User
     * @param authentication Authentication
     */
    void checkUpdatedUserByAuth(User updatedUser, Authentication authentication);

    /**
     * Удаление пользователя
     * @param user User
     */
    void delete(User user);
}
