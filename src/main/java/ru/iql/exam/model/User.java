package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность - Пользователь. Удаление - soft
 */
@Entity
@Table(schema = "iql_user", name = "users",
        uniqueConstraints = @UniqueConstraint(name = "user_email_uniqie", columnNames = {"email"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Имя
     */
    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    /**
     * Возраст
     */
    @Column(name = "age", nullable = false)
    @NotNull @Positive
    private Integer age;

    /**
     * Эл.почта
     */
    @Column(name = "email", nullable = false, unique = true)
    @NotBlank @Email
    private String email;

    /**
     * Профиль
     */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;

    /**
     * Список номеров телефонов
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @Builder.Default
    private Set<UserPhone> phones = new HashSet<>();

    /**
     * Учетные данные
     */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "credentials_id", referencedColumnName = "id")
    private UserCredentials credentials;
}
