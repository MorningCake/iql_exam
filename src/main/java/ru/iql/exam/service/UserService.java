package ru.iql.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import ru.iql.exam.exception.handler.AlreadyExistsException;
import ru.iql.exam.exception.handler.PermissionDeniedException;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

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
     * Проверить, совпадает ли обновляемый пользователь с аутентификацией
     * @param updatedUser User
     * @param authentication Authentication
     */
    void checkUpdatedUserByAuth(User updatedUser, Authentication authentication) throws PermissionDeniedException;

    /**
     * Удаление пользователя
     * @param user User
     */
    void delete(User user);

    /**
     * Проверить уникальность номеров телефона, почты и логина (при создании)
     * @param newUser User
     * @throws AlreadyExistsException
     */
    void checkUniqueFields(User newUser) throws AlreadyExistsException;

    /**
     * Проверить уникальность номеров телефона, почты и логина (при редактировании)
     * @param user User
     * @param newPhones только новые номера пользователя (исключены уже имеющиеся)
     * @param newLogin новый логин
     * @param newEmail новая почта
     * @throws AlreadyExistsException
     */
    void checkUniqueFields(User user, Set<UserPhone> newPhones, String newLogin, String newEmail) throws AlreadyExistsException;

    /**
     * Проверить уникальность почты
     * @param user User
     * @param newEmail новая почта
     * @throws AlreadyExistsException
     */
    void checkUniqueEmail(User user, String newEmail) throws AlreadyExistsException;
}
