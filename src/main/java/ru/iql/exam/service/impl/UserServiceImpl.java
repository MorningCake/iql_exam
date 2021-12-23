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
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.service.UserCredentialsService;
import ru.iql.exam.service.UserPhoneService;
import ru.iql.exam.service.UserService;
import ru.iql.exam.utils.PageUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

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
        return userRepository.save(user);
    }

    @Override
    public void checkUpdatedUserByAuth(User updatedUser, Authentication authentication)
            throws PermissionDeniedException {
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

    @Override
    public void checkUniqueFields(User user) {
        checkUniqueFields(user, user.getPhones(), user.getCredentials().getLogin(), user.getEmail());
    }

    @Override
    public void checkUniqueFields(User user, Set<UserPhone> newPhones, String newLogin, String newEmail)
            throws AlreadyExistsException
    {
        checkUniquePhones(user, newPhones);
        checkUniqueLogin(user, newLogin);
        checkUniqueEmail(user, newEmail);
    }

    /**
     * Проверить уникальность логина
     * @param user User
     * @param newLogin
     */
    private void checkUniqueLogin(User user, String newLogin) {
        credentialsService.findByLogin(newLogin).ifPresent((cr) -> {
            String message = "У пользователя " + user.getName() + " не уникален логин: " + cr.getLogin();
            log.error(message);
            throw new AlreadyExistsException(message);
        });
    }

    /**
     * Проверить уникальность телефонов
     * @param user User
     * @param newPhones только новые номера пользователя (исключены уже имеющиеся)
     */
    private void checkUniquePhones(User user, Set<UserPhone> newPhones) {
        StringBuilder stringBuilder = new StringBuilder();

        newPhones.stream()
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

    @Override
    public void checkUniqueEmail(User user, String newEmail) throws AlreadyExistsException {
        findByEmail(newEmail).ifPresent((u) -> {
            String message = "У пользователя " + user.getName() + " не уникальна эл.почта: " + u.getEmail();
            log.error(message);
            throw new AlreadyExistsException(message);
        });
    }
}
