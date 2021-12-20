package ru.iql.exam.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.iql.exam.controller.UserController;
import ru.iql.exam.mapping.dto.NewUserDto;
import ru.iql.exam.mapping.dto.SelfUpdateUserDto;
import ru.iql.exam.mapping.dto.UserDto;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.mapping.mapper.UserMapper;
import ru.iql.exam.model.User;
import ru.iql.exam.service.UserService;
import ru.iql.exam.utils.PageUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> searchWithFilters(UserSearch params) {
        Page<User> usersPage = userService.searchWithFilters(params);
        Pageable pageable = PageUtils.getPageable(params.getPageNumber(), params.getPageSize());
        List<UserDto> dtoList = userMapper.usersToDtoList(usersPage.getContent());
        return new PageImpl<>(dtoList, pageable, usersPage.getTotalElements());
    }

    @Override
    public UserDto create(NewUserDto dto) {
        return null;
    }

    @Override
    public void updateSelf(SelfUpdateUserDto dto, Long id) {

    }

    @Override
    public void update(NewUserDto dto, Long id) {

    }

    @Override
    public void delete(Long id) {

    }
}
