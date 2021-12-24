package ru.iql.exam.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iql.exam.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>/*, UserDetachable*/ {

    Page<User> findAll(Specification<User> spec, Pageable pageable);
    Optional<User> findByEmail(String email);
    List<User> findAllByProfileAutoIncrementedIsTrueAndProfileStartCashGreaterThan(int startCash);
}
