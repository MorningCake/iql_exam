package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iql.exam.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
