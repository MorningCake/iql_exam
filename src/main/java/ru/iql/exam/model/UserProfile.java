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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfile {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    @Setter
    private Long id;

    /**
     * Состояние счета, коп.
     */
    @Column(name = "cash", nullable = false)
    @NotNull
    @Setter
    private Integer cash;

    /**
     * Состояние счета при создании аккаунта, коп.
     */
    @Column(name = "start_cash", nullable = false, updatable = false)
    @NotNull
    private Integer startCash;

//    @Column(name = "auto_incremented")
//    @Setter
//    @Builder.Default
//    private boolean autoIncremented = true;

    @PrePersist
    private void initStartCash() {
        startCash = cash;
    }
}
