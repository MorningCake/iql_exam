package ru.iql.exam.service;

import org.springframework.data.domain.Page;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;

public interface UserService {

    Page<User> searchWithFilters(UserSearch params);
}
