package ru.iql.exam.dao.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import ru.iql.exam.constant.ComparisonType;
import ru.iql.exam.mapping.dto.AgeFilter;
import ru.iql.exam.mapping.dto.CashFilter;
import ru.iql.exam.model.*;

import javax.persistence.criteria.*;

import static ru.iql.exam.constant.ComparisonType.*;

/**
 * Спецификация для поиска пользователей
 */
public class UserSpec {

    /**
     * Спецификация для поиска по фильтрам
     * @param ageFilter фильтр по возрасту
     * @param cashFilter фильтр по балансу
     * @param name имя (like)
     * @param email эл.почта (like)
     * @param phone номер телефона (like)
     * @return Specification<User>
     */
    public static Specification<User> getFiltersSpec(@Nullable AgeFilter ageFilter, @Nullable CashFilter cashFilter,
                                              @Nullable String name, @Nullable String email, @Nullable String phone) {
        return (root, query, builder) -> {
            // инициализировать предикат (все записи)
            query.distinct(true);
            Predicate predicate = builder.greaterThan(root.get(User_.ID), 0);
            // если фильтры не заданы, вернуть все записи
            if (ageFilter == null && cashFilter == null && name == null && email == null && phone == null) {
                return predicate;
            }
            // если хотя бы один фильтр задан, то построить цепочку предикатов
            if (ageFilter != null) {
                predicate = addPredicateByComparisonType(
                        ageFilter.getAge(), ageFilter.getComparisonType(), root.get(User_.AGE), builder, predicate);
            }
            if (cashFilter != null) {
                Join<User, UserProfile> joinedProfile = root.join(User_.PROFILE, JoinType.INNER);
                predicate = addPredicateByComparisonType(
                        cashFilter.getCash(),
                        cashFilter.getComparisonType(),
                        joinedProfile.get(UserProfile_.CASH),
                        builder,
                        predicate
                );
            }
            if (name != null) {
                predicate = addLikePredicate(name, root.get(User_.NAME), builder, predicate);
            }
            if (email != null) {
                predicate = addLikePredicate(email, root.get(User_.EMAIL), builder, predicate);
            }
            if (phone != null) {
                Join<User, UserPhone> joinedPhone = root.joinList(User_.PHONES, JoinType.INNER);
                predicate = addLikePredicate(phone, joinedPhone.get(UserPhone_.VALUE), builder, predicate);
            }

            return predicate;
        };
    }

    /**
     * Добавить числовой предикат
     * @param value значение
     * @param comparisonType тип сравнения
     * @param path Возвращаемое значение Root.get(...) или Join.get(...)
     * @param builder CriteriaBuilder
     * @param predicate Predicate
     * @return Predicate
     */
    private static Predicate addPredicateByComparisonType(
            Integer value,
            ComparisonType comparisonType,
            Path<Integer> path,
            CriteriaBuilder builder,
            Predicate predicate
    ) {
        if (comparisonType == EQUAL) {
            predicate = builder.and(predicate, builder.equal(path, value));
        } else if (comparisonType == GREATER_THAN) {
            predicate = builder.and(predicate, builder.greaterThan(path, value));
        } else if (comparisonType == GREATER_THAN_OR_EQUAL_TO) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(path, value));
        } else if (comparisonType == LESS_THAN) {
            predicate = builder.and(predicate, builder.lessThan(path, value));
        } else {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(path, value));
        }
        return predicate;
    }

    /**
     * Добавить текстовый предикат (like)
     * @param value значение
     * @param path Возвращаемое значение Root.get(...) или Join.get(...)
     * @param builder CriteriaBuilder
     * @param predicate Predicate
     * @return Predicate
     */
    private static Predicate addLikePredicate(String value, Path<String> path, CriteriaBuilder builder, Predicate predicate) {
        predicate = builder.and(predicate, builder.like(path, "%" + value + "%"));
        return predicate;
    }
}

