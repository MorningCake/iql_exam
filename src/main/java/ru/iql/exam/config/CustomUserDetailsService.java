package ru.iql.exam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.iql.exam.config.jwt.JwtProvider;
import ru.iql.exam.model.UserCredentials;
import ru.iql.exam.service.UserCredentialsService;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialsService credentialsService;

    @Override
    public CustomUserDetails loadUserByUsername(String login) throws EntityNotFoundException {
        UserCredentials credentials = credentialsService.identification(login);
        return CustomUserDetails.fromUserEntityToCustomUserDetails(credentials);
    }
}
