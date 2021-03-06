package ru.iql.exam.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.iql.exam.constant.ComparisonType;
import ru.iql.exam.mapping.dto.*;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserCredentials;
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.model.UserProfile;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для генерации сущностей и DTO для тестов
 */
public class UserData {

    public static User createUser(Integer cash, Set<String> phoneNumbers, Integer age, String name, String email,
                                  String login, String password) {
        UserProfile profile = UserProfile.builder().cash(cash).build();

        Set<UserPhone> phones = phoneNumbers.stream()
                .map(n -> UserPhone.builder().value(n).build())
                .collect(Collectors.toSet());

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        UserCredentials credentials = UserCredentials.builder()
                .login(login)
                .password(encoder.encode(password))
                .build();

        return User.builder().age(age).name(name).email(email).profile(profile).phones(phones).credentials(credentials).build();
    }

    public static UserSearch createAllFilters(
            Integer age, ComparisonType ageComparison, Integer cash, ComparisonType cashComparison,
            String nameLike, String emailLike, String phoneLike,
            Integer pageNumber, Integer pageSize
    ) {
        AgeFilter ageFilter = AgeFilter.builder().age(age).comparisonType(ageComparison).build();
        CashFilter cashFilter = CashFilter.builder().cash(cash).comparisonType(cashComparison).build();

        return UserSearch.builder()
                .ageFilter(ageFilter)
                .cashFilter(cashFilter)
                .name(nameLike)
                .email(emailLike)
                .phone(phoneLike)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    public static UserSearch createNumericFilters(
            Integer age, ComparisonType ageComparison, Integer cash, ComparisonType cashComparison,
            Integer pageNumber, Integer pageSize
    ) {
        AgeFilter ageFilter = AgeFilter.builder().age(age).comparisonType(ageComparison).build();
        CashFilter cashFilter = CashFilter.builder().cash(cash).comparisonType(cashComparison).build();

        return UserSearch.builder()
                .ageFilter(ageFilter)
                .cashFilter(cashFilter)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    public static UserSearch createTextFilters(
            String nameLike, String emailLike, String phoneLike,
            Integer pageNumber, Integer pageSize
    ) {
        return UserSearch.builder()
                .name(nameLike)
                .email(emailLike)
                .phone(phoneLike)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    public static NewUserDto createNewUserDto(
            Integer cash, Set<String> phoneNumbers, Integer age, String name, String email,
            String login, String password
    ) {
        Set<NewUserPhoneDto> newPhonesDto = phoneNumbers.stream()
                .map(n -> NewUserPhoneDto.builder().value(n).build())
                .collect(Collectors.toSet());

        NewUserProfileDto newProfileDto = NewUserProfileDto.builder().cash(cash).build();

        NewCredentialsDto newCredentialsDto = NewCredentialsDto.builder().login(login).password(password).build();

        return NewUserDto.builder()
                .phones(newPhonesDto)
                .profile(newProfileDto)
                .credentials(newCredentialsDto)
                .age(age)
                .name(name)
                .email(email)
                .build();
    }

    public static SelfUpdateUserDto createSelfUpdateDto(String email) {
        return SelfUpdateUserDto.builder().email(email).build();
    }

}
