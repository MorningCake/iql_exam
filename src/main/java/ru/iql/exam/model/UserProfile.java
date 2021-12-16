package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = "iql_user", name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "cash", nullable = false)
    @NotNull
    private Integer cash;

    @Column(name = "start_cash", nullable = false)
    @NotNull
    private Integer startCash;
}
