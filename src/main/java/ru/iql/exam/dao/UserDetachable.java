package ru.iql.exam.dao;

import ru.iql.exam.model.User;

/**
 * Detach User
 */
public interface UserDetachable {
    void detach(User user);
}
