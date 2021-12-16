package ru.iql.exam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iql.exam.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
