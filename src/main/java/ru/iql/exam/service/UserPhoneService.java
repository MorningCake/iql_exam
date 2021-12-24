package ru.iql.exam.service;

import ru.iql.exam.model.UserPhone;

import java.util.Optional;

/**
 * Сервис для работы с номерами телефонов
 */
public interface UserPhoneService {

    /**
     * Поиск по номеру
     * @param number номер
     * @return Optional UserPhone
     */
    Optional<UserPhone> findByNumber(String number);
}
