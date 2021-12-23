package ru.iql.exam.dao.impl;

import org.springframework.stereotype.Repository;
import ru.iql.exam.dao.UserDetachable;
import ru.iql.exam.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDetachManager implements UserDetachable {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(User user) {
        entityManager.detach(user);
    }





}
