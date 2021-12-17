package ru.iql.exam.controller.impl;

import org.springframework.web.bind.annotation.RestController;
import ru.iql.exam.controller.UserController;
import ru.iql.exam.mapping.dto.NewUserDto;
import ru.iql.exam.mapping.dto.SelfUpdateUserDto;
import ru.iql.exam.mapping.dto.UserDto;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {
    @Override
    public List<UserDto> readWithFilters() {
        return null;
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
