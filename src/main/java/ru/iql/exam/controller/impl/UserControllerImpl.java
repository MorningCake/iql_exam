package ru.iql.exam.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import ru.iql.exam.controller.UserController;
import ru.iql.exam.mapping.dto.NewUserDto;
import ru.iql.exam.mapping.dto.SelfUpdateUserDto;
import ru.iql.exam.mapping.dto.UserDto;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.mapping.mapper.UserMapper;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.service.UserService;
import ru.iql.exam.utils.PageUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Transactional
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
        // маппинг + хеширование пароля
        User user = userMapper.newDtoToUserAndPassEncode(dto);
        userService.checkUniqueFields(user);
        user = userService.save(user);
        return userMapper.userToDto(user);
    }

    @Override
    public void updateSelf(SelfUpdateUserDto dto, Long id, Authentication authentication) {
        User user = userService.findById(id);
        userService.checkUpdatedUserByAuth(user, authentication);
        userService.checkUniqueEmail(user, dto.getEmail());
        user = userMapper.updateSelfFromDto(dto, user);
        userService.save(user);
    }

    @Override
    public void update(NewUserDto dto, Long id) {
        User user = userService.findById(id);
        Set<UserPhone> onlyNewPhones = userMapper.getOnlyNewPhones(dto.getPhones(), user.getPhones());
        userService.checkUniqueFields(user, onlyNewPhones, dto.getCredentials().getLogin(), dto.getEmail());
        user = userMapper.updateUserFromNewDtoAndPassEncode(dto, user);
        userService.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userService.findById(id);
        userService.delete(user);
    }
}
