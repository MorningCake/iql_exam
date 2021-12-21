package ru.iql.exam.mapping.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {

    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
