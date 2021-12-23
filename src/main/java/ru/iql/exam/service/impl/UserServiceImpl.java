package ru.iql.exam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.dao.spec.UserSpec;
import ru.iql.exam.exception.handler.AlreadyExistsException;
import ru.iql.exam.exception.handler.PermissionDeniedException;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;
import ru.iql.exam.service.UserCredentialsService;
import ru.iql.exam.service.UserPhoneService;
import ru.iql.exam.service.UserService;
import ru.iql.exam.utils.PageUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPhoneService userPhoneService;
    private final UserCredentialsService credentialsService;

    @Override
    public Page<User> searchWithFilters(UserSearch params) {
        Specification<User> userFiltersSpec = UserSpec.getFiltersSpec(
                params.getAgeFilter(), params.getCashFilter(), params.getName(), params.getEmail(), params.getPhone()
        );
        Pageable pageable = PageUtils.getPageable(params.getPageNumber(), params.getPageSize());
        return userRepository.findAll(userFiltersSpec, pageable);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) throws EntityNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EntityNotFoundException("Пользователь ID " + id + " не найден!");
        }
    }

    @Override
    public User save(User user) {
        checkUniqueFields(user);
        return userRepository.save(user);
    }

    @Override
    public User update(User user, String oldEmail, String oldLogin) {
        checkUniqueFieldsExcludeOldSelfAndPhonesMerging(user, oldEmail, oldLogin);
        return userRepository.save(user);
    }

    @Override
    public void updateSelf(User user, String oldEmail) {
        checkUniqueEmailExcludeSelf(user, oldEmail);
        userRepository.save(user);
    }

    @Override
    public void checkUpdatedUserByAuth(User updatedUser, Authentication authentication) {
        if (!updatedUser.getCredentials().getLogin().equals(authentication.getName())) {
            log.error("Попытка пользователя " + authentication.getName() + " изменить данные пользователя ID" +
                    updatedUser.getCredentials().getLogin());
            throw new PermissionDeniedException("Запрещено менять данные других пользователей!");
        }
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Проверить уникальность номеров телефона, почты и логина
     * @param user User
     */
    private void checkUniqueFields(User user) throws AlreadyExistsException {
        checkUniquePhones(user);

        credentialsService.findByLogin(user.getCredentials().getLogin()).ifPresent((cr) -> {
            String message = "У пользователя " + user.getName() + " не уникален логин: " + cr.getLogin();
            log.error(message);
            throw new AlreadyExistsException(message);
        });

        findByEmail(user.getEmail()).ifPresent((u) -> {
            String message = "У пользователя " + user.getName() + " не уникальна эл.почта: " + u.getEmail();
            log.error(message);
            throw new AlreadyExistsException(message);
        });
    }

    /**
     * Проверить уникальность номеров телефона, почты и логина, исключая свои старые данные
     * Также смержить
     * @param user User
     * @param oldEmail
     * @param oldLogin
     * @throws AlreadyExistsException
     */
    private void checkUniqueFieldsExcludeOldSelfAndPhonesMerging(User user, String oldEmail, String oldLogin)
            throws AlreadyExistsException
    {
        checkUniquePhones(user);

        // исключим старый логин
        if (!oldLogin.equals(user.getCredentials().getLogin())) {
            credentialsService.findByLogin(user.getCredentials().getLogin()).ifPresent((cr) -> {
                String message = "У пользователя " + user.getName() + " не уникален логин: " + cr.getLogin();
                log.error(message);
                throw new AlreadyExistsException(message);
            });
        }
        // исключим старую почту
        checkUniqueEmailExcludeSelf(user, oldEmail);
    }

    /**
     * Проверить уникальность телефонов
     * @param user User
     */
    private void checkUniquePhones(User user) {
        StringBuilder stringBuilder = new StringBuilder();

        user.getPhones().stream()
                .map(n -> userPhoneService.findByNumber(n.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(n -> stringBuilder.append(n.getValue()).append(", "));

        if (stringBuilder.length() > 0) {
            String existsNumbers = stringBuilder.toString().substring(0, stringBuilder.length() - 2);
            String message = "У пользователя " + user.getName() + " не уникальны номера телефона: " + existsNumbers;
            log.error(message);
            throw new AlreadyExistsException(message);
        }
    }

    /**
     * Проверить уникальность почты, исключая свои данные
     * @param user User
     * @param oldEmail
     * @throws AlreadyExistsException
     */
    private void checkUniqueEmailExcludeSelf(User user, String oldEmail) throws AlreadyExistsException {
        if (!oldEmail.equals(user.getEmail())) {
            if (findByEmail(user.getEmail()).isPresent()) {
                String message = "У пользователя " + user.getName() + " не уникальна эл.почта: " + user.getEmail();
                log.error(message);
                throw new AlreadyExistsException(message);
            }
        }
    }
}
