package ru.iql.exam.mapping.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import ru.iql.exam.mapping.dto.*;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    NewUserPhoneDto phoneToNewDto(UserPhone phone);
//    UserPhone phoneToNewDto(NewUserPhoneDto dto);

    UserPhoneDto phoneToDto(UserPhone phone);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    List<UserPhoneDto> phonesToDtoList(List<UserPhone> phones);

    BaseUserDto userToBaseDto(User user);
    User baseDtoToUser(BaseUserDto dto);

//    NewUserDto userToNewDto(User user);
    User newDtoToUser(NewUserDto dto);

    UserDto userToDto(User user);
    User dtoToUser(NewUserDto dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    List<UserDto> usersToDtoList(List<User> users);
}
