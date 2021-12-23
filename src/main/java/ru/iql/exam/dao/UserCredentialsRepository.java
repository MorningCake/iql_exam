package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iql.exam.model.UserCredentials;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long>, JpaSpecificationExecutor<UserCredentials> {

    Optional<UserCredentials> findByLogin(String login);
}
