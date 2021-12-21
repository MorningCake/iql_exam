package ru.iql.exam.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.iql.exam.config.jwt.JwtProvider;
import ru.iql.exam.constant.UserRole;
import ru.iql.exam.dao.UserCredentialsRepository;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.exception.handler.AuthenticationFailedException;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserCredentials;
import ru.iql.exam.service.UserCredentialsService;
import ru.iql.exam.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final UserCredentialsRepository credentialsRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserCredentials identification(String login) throws EntityNotFoundException {
        Optional<UserCredentials> optionalCreds = credentialsRepository.findByLogin(login);
        if (optionalCreds.isPresent()) {
            return optionalCreds.get();
        } else {
            throw new EntityNotFoundException("Учетные данные пользователя не найдены!");
        }
    }

    @Override
    public String auth(String login, String password) {
        UserCredentials creds = identification(login);
        if (!passwordEncoder.matches(password, creds.getPassword())) {
            throw new AuthenticationFailedException("Пароль неверный!");
        }
        return jwtProvider.generateToken(creds.getLogin());
    }
}
