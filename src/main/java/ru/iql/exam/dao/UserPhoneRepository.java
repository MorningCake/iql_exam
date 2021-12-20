package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iql.exam.model.UserPhone;

public interface UserPhoneRepository extends JpaRepository<UserPhone, Long>, JpaSpecificationExecutor<UserPhone> {
}
