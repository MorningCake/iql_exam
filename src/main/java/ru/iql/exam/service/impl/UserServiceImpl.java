package ru.iql.exam.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.dao.spec.UserSpec;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;
import ru.iql.exam.service.UserService;
import ru.iql.exam.utils.PageUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<User> searchWithFilters(UserSearch params) {
        Specification<User> userFiltersSpec = UserSpec.getFiltersSpec(
                params.getAgeFilter(), params.getCashFilter(), params.getName(), params.getEmail(), params.getPhone()
        );
        Pageable pageable = PageUtils.getPageable(params.getPageNumber(), params.getPageSize());
        return userRepository.findAll(userFiltersSpec, pageable);
    }

    @Override
    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EntityNotFoundException("Пользователь не найден!");
        }
    }


}
