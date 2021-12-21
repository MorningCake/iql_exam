package ru.iql.exam.controller.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.iql.exam.mapping.dto.AuthRequest;
import ru.iql.exam.mapping.dto.AuthResponse;
import ru.iql.exam.service.UserCredentialsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserCredentialsService credentialsService;

    @PostMapping("/api/auth")
    public AuthResponse auth(@RequestBody @Valid AuthRequest request) {
        String token = credentialsService.auth(request.getLogin(), request.getPassword());
        return new AuthResponse(token);
    }
}
