package ru.iql.exam.common;

import ru.iql.exam.constant.ComparisonType;
import ru.iql.exam.mapping.dto.AgeFilter;
import ru.iql.exam.mapping.dto.CashFilter;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.model.UserProfile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для генерации сущностей и DTO для тестов
 */
public class UserData {

    public static User createUser(Integer cash, Set<String> phoneNumbers, Integer age, String name, String email) {
        UserProfile profile = UserProfile.builder().cash(cash).build();

        List<UserPhone> phones = phoneNumbers.stream()
                .map(n -> UserPhone.builder().value(n).build())
                .collect(Collectors.toList());

        return User.builder().age(age).name(name).email(email).profile(profile).phones(phones).build();
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
}
