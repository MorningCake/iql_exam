package ru.iql.exam.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "iql_user", name = "department")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
}
