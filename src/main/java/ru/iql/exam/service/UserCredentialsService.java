package ru.iql.exam.service;

import ru.iql.exam.model.UserCredentials;

import java.util.Optional;

/**
 * Сервис для работы с учетными данными
 */
public interface UserCredentialsService {

    /**
     * Найти креды по логину
     * @param login логин
     * @return Optional UserCredentials
     */
    Optional<UserCredentials> findByLogin(String login);

    /**
     * Идентификация
     * @param login логин
     * @return UserCredentials
     */
    UserCredentials identification(String login);

    /**
     * Аутентификация
     * @param login логин
     * @param password пароль
     * @return токен
     */
    String auth(String login, String password);
}
