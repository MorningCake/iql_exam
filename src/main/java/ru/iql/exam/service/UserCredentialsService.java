package ru.iql.exam.service;

import ru.iql.exam.model.UserCredentials;

/**
 * Сервис для работы с учетными данными
 */
public interface UserCredentialsService {

    UserCredentials identification(String login);

    String auth(String login, String password);
}
