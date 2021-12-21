package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iql.exam.model.UserCredentials;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByLogin(String login);
}
