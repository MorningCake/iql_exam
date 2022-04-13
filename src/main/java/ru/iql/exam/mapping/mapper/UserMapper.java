package ru.iql.exam.mapping.mapper;

import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.iql.exam.mapping.dto.*;
import ru.iql.exam.model.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//todo убрать лишнее, test

@Mapper(componentModel = "spring")
public interface UserMapper {
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    UserPhoneDto phoneToDto(UserPhone phone);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    Set<UserPhoneDto> phonesToDtoList(Set<UserPhone> phones);

    UserProfile newDtoToProfile(NewUserProfileDto dto);
    UserProfileDto profileToDto(UserProfile profile);

    @Mapping(target = "department.id", source = "departmentId")
    User newDtoToUserAndPassEncode(NewUserDto dto);

    //@Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentDto", source = "department", qualifiedByName = "Department")
    UserDto userToDto(User user);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    List<UserDto> usersToDtoList(List<User> users);

    @Named("passwordToHash")
    default String passwordToHash(String password) {
        return encoder.encode(password);
    }

    @Mapping(target = "password", source = "password", qualifiedByName = "passwordToHash")
    UserCredentials dtoToCredsAndPassEncode(NewCredentialsDto dto);

    @Named("updateCredentials")
    @Mapping(target = "password", source = "password", qualifiedByName = "passwordToHash")
    UserCredentials updateCredsFromNewDto(NewCredentialsDto dto, @MappingTarget UserCredentials credentials);

    @Named("updateProfile")
    UserProfile updateProfileFromNewDto(NewUserProfileDto dto, @MappingTarget UserProfile profile);

    UserPhone newDtoToPhone(NewUserPhoneDto dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    Set<UserPhone> newDtosToPhones(Set<NewUserPhoneDto> dtos);

    /**
     * Получить только не используемые ранее пользователем телефоны
     * @param newDtos список новых телефонов
     * @param oldPhones список имеющихся телефонов
     * @return список не используемых ранее телефонов
     */
    default Set<UserPhone> getOnlyNewPhones(Set<NewUserPhoneDto> newDtos, Set<UserPhone> oldPhones) {
        Set<UserPhone> newPhones = this.newDtosToPhones(newDtos);
        Set<UserPhone> intersectionsFromOld =
                oldPhones.stream().filter(newPhones::contains).collect(Collectors.toSet());
        newPhones.removeAll(intersectionsFromOld);
        return newPhones;
    }

    @Named("updatePhones")
    default Set<UserPhone> updatePhones(Set<NewUserPhoneDto> dtos, @MappingTarget Set<UserPhone> phones) {
        Set<UserPhone> newPhones = this.newDtosToPhones(dtos);
        Set<UserPhone> intersectionsFromOld =
                phones.stream().filter(newPhones::contains).collect(Collectors.toSet());
        newPhones.removeAll(intersectionsFromOld);
        phones.clear();
        phones.addAll(newPhones);
        phones.addAll(intersectionsFromOld);
        return phones;
    }

    @Mapping(target = "credentials", source = "credentials", qualifiedByName = "updateCredentials")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "updateProfile")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "updatePhones")
    @Mapping(target = "department.id", source = "departmentId")
    User updateUserFromNewDtoAndPassEncode(NewUserDto dto, @MappingTarget User user);

    User updateSelfFromDto(SelfUpdateUserDto dto, @MappingTarget User user);

    @Named("Department")
    DepartmentDto toDepartmentDto(Department dep);
}
