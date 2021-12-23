package ru.iql.exam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.iql.exam.model.UserCredentials;
import ru.iql.exam.service.UserCredentialsService;

import javax.persistence.EntityNotFoundException;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialsService credentialsService;

    @Override
    public CustomUserDetails loadUserByUsername(String login) throws EntityNotFoundException {
        UserCredentials credentials = credentialsService.identification(login);
        return CustomUserDetails.fromUserEntityToCustomUserDetails(credentials);
    }
}
