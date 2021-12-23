package ru.iql.exam.mapping.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.iql.exam.common.UserData;
import ru.iql.exam.mapping.dto.NewUserDto;
import ru.iql.exam.mapping.dto.SelfUpdateUserDto;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест маппера")
class UserMapperTest {

    private static final Integer CASH = 100_00;
    private static final String PHONE_NUM_1 = "+79050000001";
    private static final String PHONE_NUM_2 = "+79270000002";
    private static final Integer AGE = 30;
    private static final String NAME = "Name";
    private static final String EMAIL = "email@mail.ru";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "a";

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Тест маппинга NewUserDto -> User")
    void newDtoToUser() {
        NewUserDto newUserDto =
                UserData.createNewUserDto(CASH, Set.of(PHONE_NUM_1, PHONE_NUM_2), AGE, NAME, EMAIL, LOGIN, PASSWORD);
        User actual = userMapper.newDtoToUserAndPassEncode(newUserDto);

        assertThat(actual.getId()).isNull();
        assertThat(actual.getProfile().getCash()).isEqualTo(CASH);
        assertThat(actual.getPhones().size()).isEqualTo(2);
        assertThat(actual.getAge()).isEqualTo(AGE);
        assertThat(actual.getName()).isEqualTo(NAME);
        assertThat(actual.getEmail()).isEqualTo(EMAIL);
        assertThat(actual.getCredentials().getLogin()).isEqualTo(LOGIN);
        assertThat(encoder.matches(PASSWORD, actual.getCredentials().getPassword())).isTrue();
    }

    @Test
    @DisplayName("Тест обновления полей User из NewUserDto")
    void updateUserFromDto() {
        NewUserDto updateDto =
                UserData.createNewUserDto(CASH, Set.of(PHONE_NUM_1, PHONE_NUM_2), AGE, NAME, EMAIL, LOGIN, PASSWORD);
        User target = UserData.createUser(100_00, Set.of(PHONE_NUM_1, "+79270000001"), 20, "User1", "email1@email.ru",
                "u1", "p1");
        target.setId(1L);
        target.setPhones(target.getPhones().stream().peek(ph -> ph.setId(2L)).collect(Collectors.toSet()));
        target.getProfile().setId(3L);
        target.getCredentials().setId(4L);

        User actual = userMapper.updateUserFromNewDtoAndPassEncode(updateDto, target);

        assertThat(actual.getProfile().getCash()).isEqualTo(CASH);
        assertThat(actual.getPhones().size()).isEqualTo(2);

        Optional<UserPhone> phone1 = actual.getPhones().stream().filter(p -> p.getValue().equals(PHONE_NUM_1)).findAny();
        assertThat(phone1.isPresent()).isTrue();
        assertThat(phone1.get().getId()).isEqualTo(2L);

        Optional<UserPhone> phone2 = actual.getPhones().stream().filter(p -> p.getValue().equals(PHONE_NUM_2)).findAny();
        assertThat(phone2.isPresent()).isTrue();
        assertThat(phone2.get().getId()).isNull();

        assertThat(actual.getAge()).isEqualTo(AGE);
        assertThat(actual.getName()).isEqualTo(NAME);
        assertThat(actual.getEmail()).isEqualTo(EMAIL);
        assertThat(actual.getCredentials().getLogin()).isEqualTo(LOGIN);
        assertThat(encoder.matches(PASSWORD, actual.getCredentials().getPassword())).isTrue();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getProfile().getId()).isEqualTo(3L);
        assertThat(actual.getCredentials().getId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("Тест обновления полей User из SelfUpdateUserDto")
    void updateSelfFromDto() {
        SelfUpdateUserDto dto = UserData.createSelfUpdateDto("update@mail.ru");
        User target = UserData.createUser(CASH, Set.of(PHONE_NUM_1, PHONE_NUM_2), AGE, NAME, EMAIL, LOGIN, PASSWORD);

        User actual = userMapper.updateSelfFromDto(dto, target);

        assertThat(actual.getProfile().getCash()).isEqualTo(CASH);
        assertThat(actual.getPhones().size()).isEqualTo(2);
        assertThat(actual.getAge()).isEqualTo(AGE);
        assertThat(actual.getName()).isEqualTo(NAME);
        assertThat(actual.getEmail()).isEqualTo(dto.getEmail());
        assertThat(actual.getCredentials().getLogin()).isEqualTo(LOGIN);
        assertThat(encoder.matches(PASSWORD, actual.getCredentials().getPassword())).isTrue();
    }
}