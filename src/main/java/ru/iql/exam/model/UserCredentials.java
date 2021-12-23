package ru.iql.exam.model;

import lombok.*;
import ru.iql.exam.constant.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Сущность - Учетные данные
 */
@Entity
@Table(schema = "iql_user", name = "credentials",
        uniqueConstraints = @UniqueConstraint(name = "user_login_uniqie", columnNames = {"login"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserCredentials {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Логин
     */
    @Column(name = "login", nullable = false, updatable = false, unique = true)
    @NotBlank
    private String login;

    /**
     * Пароль / Хэш (на БД)
     */
    @Column(name = "hash", nullable = false)
    @NotBlank
    private String password;

    /**
     * Роль
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * Пользователь
     */
    @OneToOne(mappedBy = "credentials")
    private User user;
}
