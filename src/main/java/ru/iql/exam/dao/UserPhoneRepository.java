package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iql.exam.model.UserPhone;

public interface UserPhoneRepository extends JpaRepository<UserPhone, Long> {
}
