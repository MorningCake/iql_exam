package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Номер телефона. Должен быть уникальным
 */
@Entity
@Table(schema = "iql_user", name = "phones",
        uniqueConstraints = @UniqueConstraint(name = "phone_value_uniqie", columnNames = {"value"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id"})
public class UserPhone {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "value", nullable = false, unique = true)
    @NotBlank
    @Pattern(regexp = "^\\+7[\\d]{10}$")
    private String value;
}
