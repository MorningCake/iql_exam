package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Сущность - профиль пользователя
 */
@Entity
@Table(schema = "iql_user", name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id"})
public class UserProfile {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Состояние счета, коп.
     */
    @Column(name = "cash", nullable = false)
    @NotNull
    private Integer cash;

    /**
     * Состояние счета при создании аккаунта, коп.
     */
    @Column(name = "start_cash", nullable = false)
    @NotNull
    private Integer startCash;
}
