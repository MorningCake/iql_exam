package ru.iql.exam.model;

import lombok.*;
import ru.iql.exam.constant.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
@EqualsAndHashCode(exclude = {"id"})
public class UserCredentials {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Логин
     */
    @Column(name = "login", nullable = false, updatable = false, unique = true)
    private String login;

    /**
     * Хэш пароля
     */
    @Column(name = "hash", nullable = false)
    private String password;

    /**
     * Роль
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    /**
     * Пользователь
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
