package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность - Пользователь. Soft Delete
 */
@Entity
@Table(schema = "iql_user", name = "users",
        uniqueConstraints = @UniqueConstraint(name = "user_email_uniqie", columnNames = {"email"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id"})
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "age", nullable = false)
    @NonNull
    @Positive
    private Integer age;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserPhone> phones = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
