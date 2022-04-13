package ru.iql.exam.controller.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.hibernate.resource.transaction.internal.TransactionCoordinatorBuilderInitiator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.iql.exam.common.UserData;
import ru.iql.exam.dao.UserCredentialsRepository;
import ru.iql.exam.dao.UserProfileRepository;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.exception.AlreadyExistsException;
import ru.iql.exam.exception.EntityNotFoundException;
import ru.iql.exam.exception.PermissionDeniedException;
import ru.iql.exam.mapping.dto.NewUserDto;
import ru.iql.exam.mapping.dto.SelfUpdateUserDto;
import ru.iql.exam.mapping.dto.UserDto;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.model.User;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.iql.exam.constant.ComparisonType.*;

@SpringBootTest
@AutoConfigureMockMvc
//@AutoConfigureEmbeddedDatabase(provider = OPENTABLE)
@DisplayName("Тест контроллера пользователей")
//@Transactional
class UserControllerTest {

    @Autowired private DataSource dataSource;
    @Autowired private UserRepository userRepository;
    @Autowired private UserCredentialsRepository credentialsRepository;
    @Autowired private UserProfileRepository profileRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final String SEARCH_URI = "/api/users/search/filters";
    private static final String USERS_URI = "/api/users/";
    private static final String SELF_URI = "/api/users/self/";
    private static final String INCORRECT_URI = "/incorrect/uri";

    private static final String EMAIL = "email1@email.ru";
    private static final String EMAIL_2 = "email2@email.ru";
    private static final String PHONE = "+79270000001";
    private static final String PHONE_2 = "+79220000001";
    private static final String LOGIN = "u1";
    private static final String LOGIN_2 = "u2";

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    @BeforeEach
    void init() {
        user1 = UserData.createUser(100_00, Set.of(PHONE), 20, "User1", EMAIL,
                LOGIN, "p1");
        user2 = UserData.createUser(220_00, Set.of(PHONE_2), 18, "User2", EMAIL_2,
                LOGIN_2, "p2");
        user3 = UserData.createUser(0,      Set.of("+79250000002"), 20, "User3", "email3@email.ru",
                "u3", "p3");
        user4 = UserData.createUser(300_00, Set.of("+79270000002"), 20, "User4", "email4@gmail.ru",
                "u4", "p4");
        user5 = UserData.createUser(500_00, Set.of("+79660000001"), 20, "User5", "email5@email.ru",
                "u5", "p5");
        user6 = UserData.createUser(20_00,  Set.of("+79290000003"), 30, "Admin", "email6@email.ru",
                "u6", "p6");

        userRepository.deleteAll();  //удалить админа, добавленного миграцией
        profileRepository.deleteAll();
        credentialsRepository.deleteAll();
        userRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6));
        assertThat(userRepository.count()).isEqualTo(6);
    }

    @AfterEach
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void clearing() {
        userRepository.deleteAll();
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @WithMockUser(authorities = "USER")
    @Test
    @DisplayName("Тест фильтров 1 - по всем полям - успех")
    void searchWithFilters_allFields_success() throws Exception {
        UserSearch searchParams = UserData.createAllFilters(
                20, GREATER_THAN_OR_EQUAL_TO, 50_00, GREATER_THAN,
                "User", "@email", "+792",
                0, 10);

        TestPage<UserDto> actualPage = createSearchPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        // выборочная проверка
        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user1.getName());
        assertThat(actual.getPhones().size()).isEqualTo(user1.getPhones().size());
        assertThat(actual.getProfile().getCash()).isEqualTo(user1.getProfile().getCash());
    }

    @WithMockUser(authorities = "USER")
    @Test
    @DisplayName("Тест фильтров 2 - выборочно (возраст и баланс) - успех")
    void searchWithFilters_byAgeAndCash_success() throws Exception {
        UserSearch searchParams = UserData.createNumericFilters(
                50, LESS_THAN_OR_EQUAL_TO, 10_00, LESS_THAN,
                0, 10);

        TestPage<UserDto> actualPage = createSearchPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user3.getName());
        assertThat(actual.getAge()).isEqualTo(user3.getAge());
        assertThat(actual.getProfile().getCash()).isEqualTo(user3.getProfile().getCash());
    }

    @WithMockUser(authorities = "USER")
    @Test
    @DisplayName("Тест фильтров 3 - выборочно (текстовый) - успех")
    void searchWithFilters_textFields_success() throws Exception {
        UserSearch searchParams = UserData.createTextFilters(
                "User", "@gmail", "+7",
                0, 5);

        TestPage<UserDto> actualPage = createSearchPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user4.getName());
    }

    @WithMockUser(authorities = "USER")
    @Test
    @DisplayName("Тест фильтров 4 - выборочно (возврат списка) - успех")
    void searchWithFilters_textFields_success_resultList() throws Exception {
        UserSearch searchParams = UserData.createTextFilters(
                "User", "mail", "+7",
                0, 7);

        TestPage<UserDto> actualPage = createSearchPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(5);
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Тест фильтров 5 - тест валидации - исключение")
    void searchWithFilters_validation_ex() throws Exception {
        // заполним с максимальным количеством ошибок валидации (3)
        UserSearch searchParams = UserData.createAllFilters(
                0, GREATER_THAN_OR_EQUAL_TO, -10_00, GREATER_THAN,
                "User", "mail", "+7",
                -1, 0);

        Exception ex = createIncorrectPostRequest(SEARCH_URI, status().isBadRequest(), searchParams);

        assertThat(ex instanceof MethodArgumentNotValidException).isTrue();
        assertThat(((MethodArgumentNotValidException) ex).getAllErrors().size()).isEqualTo(3);
    }

    @WithMockUser(authorities = {"ADMIN"})
    @Test
    @DisplayName("Тест фильтров 6 - некорректный uri - исключение")
    void searchWithFilters_incorrectUri_ex() throws Exception {
        UserSearch searchParams = UserData.createAllFilters(
                20, GREATER_THAN_OR_EQUAL_TO, 50_00, GREATER_THAN,
                "User", "mail", "+7",
                0, 10);

        Exception ex = createIncorrectPostRequest(INCORRECT_URI, status().isNotFound(), searchParams);
    }

    @Transactional
    @WithMockUser(authorities = {"ADMIN"})
    @Test
    @DisplayName("Создание пользователя - успех")
    void create_success() throws Exception {
        NewUserDto newUserDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                "name@mail.ru", "name7", "pass");

        UserDto actual = createPostRequest(USERS_URI, newUserDto);

        // выборочная проверка по полям
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(newUserDto.getName());
        assertThat(actual.getEmail()).isEqualTo(newUserDto.getEmail());
        assertThat(actual.getPhones().size()).isEqualTo(newUserDto.getPhones().size());

        // проверка на БД
        checkFromDbInNewTransaction(actual);
    }

//    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    void checkFromDbInNewTransaction(UserDto actual) {
        userRepository.flush();
        User actualFromDb = userRepository.getById(actual.getId());
        assertThat(actualFromDb.getName()).isEqualTo(actual.getName());
        assertThat(actualFromDb.getEmail()).isEqualTo(actual.getEmail());
        assertThat(actualFromDb.getPhones().size()).isEqualTo(actual.getPhones().size());
        assertThat(actualFromDb.getDepartment().getId()).isEqualTo(actual.getDepartmentDto().getId());
        assertThat(actualFromDb.getDepartment().getName()).isNotNull();
    }

    @WithMockUser(authorities = {"ADMIN"})
    @Test
    @DisplayName("Создание пользователя - дублирование номера телефона - исключение")
    void create_phoneDuplicate_ex() throws Exception {
        NewUserDto newUserDto = UserData.createNewUserDto(1000_00, Set.of(PHONE), 26, "Name",
                "name@mail.ru", "name7", "pass");

        Exception ex = createIncorrectPostRequest(USERS_URI, status().isConflict(), newUserDto);
        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = {"ADMIN"})
    @Test
    @DisplayName("Создание пользователя - дублирование эл.почты - исключение")
    void create_emailDuplicate_ex() throws Exception {
        NewUserDto newUserDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                EMAIL, "name7", "pass");

        Exception ex = createIncorrectPostRequest(USERS_URI, status().isConflict(), newUserDto);
        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Создание пользователя - тест валидации - исключение")
    void create_validation_ex() throws Exception {
        // заполним с максимальным количеством ошибок валидации (3)
        NewUserDto newUserDto = UserData.createNewUserDto(1000_00, Set.of("+7927"), 0, "",
                "email", "", "");

        Exception ex = createIncorrectPostRequest(USERS_URI, status().isBadRequest(), newUserDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof MethodArgumentNotValidException).isTrue();
        assertThat(((MethodArgumentNotValidException) ex).getAllErrors().size()).isEqualTo(6);
    }

    @WithMockUser(authorities = {"USER"}, username = "u1")
    @Test
    @DisplayName("Обновление собственных данных - успех")
    void updateSelf_success() throws Exception {
        // от лица user1
        SelfUpdateUserDto dto = UserData.createSelfUpdateDto("update@gmail.com");

        createPutRequest(SELF_URI + user1.getId(), dto);

        Optional<User> actual = userRepository.findByEmail(dto.getEmail());
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(user1);
    }

    @WithMockUser(authorities = {"USER"}, username = "u1")
    @Test
    @DisplayName("Обновление собственных данных - некорректный id - исключение")
    void updateSelf_wrongId_ex() throws Exception {
        // от лица user1 попробуем изменить данные user2
        SelfUpdateUserDto dto = UserData.createSelfUpdateDto("update@gmail.com");

        Exception ex = createIncorrectPutRequest(SELF_URI + user2.getId(), status().isForbidden(), dto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof PermissionDeniedException).isTrue();
    }

    @WithMockUser(authorities = {"USER"}, username = "u1")
    @Test
    @DisplayName("Обновление собственных данных - тест валидации - исключение")
    void updateSelf_validation_ex() throws Exception {
        // от лица user1
        SelfUpdateUserDto dto = UserData.createSelfUpdateDto("update");

        Exception ex = createIncorrectPutRequest(SELF_URI + user1.getId(), status().isBadRequest(), dto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof MethodArgumentNotValidException).isTrue();
        assertThat(((MethodArgumentNotValidException) ex).getAllErrors().size()).isEqualTo(1);
    }

    @WithMockUser(authorities = {"USER"}, username = "u1")
    @Test
    @DisplayName("Обновление собственных данных - неуникальная почта - исключение")
    void updateSelf_duplicate_ex() throws Exception {
        // от лица user1 попробуем записать user2.email
        SelfUpdateUserDto dto = UserData.createSelfUpdateDto(EMAIL_2);

        Exception ex = createIncorrectPutRequest(SELF_URI + user1.getId(), status().isConflict(), dto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - успех")
    void update_success() throws Exception {
        // обновим данные user1
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                "name@mail.ru", "name7", "pass");

        createPutRequest(USERS_URI + user1.getId(), updateDto);

        // выборочная проверка на БД
        User actualFromDb = userRepository.getById(user1.getId());
        assertThat(actualFromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(actualFromDb.getEmail()).isEqualTo(updateDto.getEmail());
        assertThat(actualFromDb.getPhones().size()).isEqualTo(updateDto.getPhones().size());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - неуникальный логин - исключение")
    void update_loginDuplicate_ex() throws Exception {
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                "name@mail.ru", LOGIN_2, "pass");

        Exception ex = createIncorrectPutRequest(USERS_URI + user1.getId(), status().isConflict(), updateDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - неуникальный телефон - исключение")
    void update_phoneDuplicate_ex() throws Exception {
        // попытка редактировать user1 с телефоном user2
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of(PHONE_2), 26, "Name",
                "name@mail.ru", "name7", "pass");

        Exception ex = createIncorrectPutRequest(USERS_URI + user1.getId(), status().isConflict(), updateDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - неуникальная почта - исключение")
    void update_emailDuplicate_ex() throws Exception {
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                EMAIL_2, "name7", "pass");

        Exception ex = createIncorrectPutRequest(USERS_URI + user1.getId(), status().isConflict(), updateDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof AlreadyExistsException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - некорректный id - исключение")
    void update_incorrectId_ex() throws Exception {
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of("+79270000005"), 26, "Name",
                "name@mail.ru", "name7", "pass");

        Exception ex = createIncorrectPutRequest(USERS_URI + "10000", status().isNotFound(), updateDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof EntityNotFoundException).isTrue();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Редактирование пользователя - тест валидации - исключение")
    void update_validation_ex() throws Exception {
        NewUserDto updateDto = UserData.createNewUserDto(1000_00, Set.of("+792"), 0, "",
                "mail", "", "");

        Exception ex = createIncorrectPutRequest(USERS_URI + user1.getId(), status().isBadRequest(), updateDto);

        assertThat(ex).isNotNull();
        assertThat(ex instanceof MethodArgumentNotValidException).isTrue();
        assertThat(((MethodArgumentNotValidException) ex).getAllErrors().size()).isEqualTo(6);
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Удаление пользователя - успех")
    void delete_success() throws Exception {
        createDeleteRequest(USERS_URI + user1.getId());

        assertThat(userRepository.count()).isEqualTo(5);
        assertThat(userRepository.findById(user1.getId()).isPresent()).isFalse();
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    @DisplayName("Удаление пользователя - некорректный id - исключение")
    void delete_incorrectId_ex() throws Exception {
        Exception ex = createIncorrectDeleteRequest(USERS_URI + "10000", status().isNotFound());

        assertThat(ex).isNotNull();
        assertThat(ex instanceof EntityNotFoundException).isTrue();
    }

    /**
     * Создать POST запрос для поиска
     * @param searchData UserSearch
     * @param uri API uri
     * @return TestPage<UserDto>
     * @throws Exception
     */
    private TestPage<UserDto> createSearchPostRequest(String uri, Object searchData) throws Exception {
        var request = objectMapper.writeValueAsString(searchData);

        var response = mockMvc.perform(
                post(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readValue(
                response.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {});
    }

    /**
     * Создать POST запрос
     * @param requestData Object
     * @param uri API uri
     * @return TestPage UserDto
     * @throws Exception
     */
    private UserDto createPostRequest(String uri, Object requestData) throws Exception {
        var request = objectMapper.writeValueAsString(requestData);

        var response = mockMvc.perform(
                post(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readValue(
                response.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {});
    }

    /**
     * Создать PUT запрос
     * @param requestData Object
     * @param uri API uri
     * @throws Exception
     */
    private void createPutRequest(String uri, Object requestData) throws Exception {
        var request = objectMapper.writeValueAsString(requestData);

        mockMvc.perform(
                put(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
    }

    /**
     * Создать DELETE запрос
     * @param uri API uri
     * @throws Exception
     */
    private void createDeleteRequest(String uri) throws Exception {
        mockMvc.perform(delete(uri))
                .andExpect(status().isOk()).andReturn();
    }

    /**
     * Создать некорректный POST запрос
     * @param uri API uri
     * @param status ResultMatcher
     * @param requestData UserSearch
     * @return Exception
     * @throws Exception
     */
    private Exception createIncorrectPostRequest(String uri, ResultMatcher status, Object requestData)
            throws Exception {
        var request = objectMapper.writeValueAsString(requestData);

        return mockMvc.perform(
                post(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status).andReturn().getResolvedException();
    }

    /**
     * Создать некорректный PUT запрос
     * @param uri API uri
     * @param status ResultMatcher
     * @param requestData UserSearch
     * @return Exception
     * @throws Exception
     */
    private Exception createIncorrectPutRequest(String uri, ResultMatcher status, Object requestData)
            throws Exception {
        var request = objectMapper.writeValueAsString(requestData);

        return mockMvc.perform(
                put(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status).andReturn().getResolvedException();
    }

    /**
     * Создать некорректный DELETE запрос
     * @param uri API uri
     * @param status ResultMatcher
     * @return Exception
     * @throws Exception
     */
    private Exception createIncorrectDeleteRequest(String uri, ResultMatcher status) throws Exception {
        return mockMvc.perform(delete(uri))
                .andExpect(status).andReturn().getResolvedException();
    }

    /**
     * Пагинация json
     * @param <T>
     */
    static class TestPage<T> extends PageImpl<T> {

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public TestPage(@JsonProperty("content") List<T> content,
                        @JsonProperty("number") int number,
                        @JsonProperty("size") int size,
                        @JsonProperty("totalElements") Long totalElements,
                        @JsonProperty("pageable") Object pageable,
                        @JsonProperty("last") boolean last,
                        @JsonProperty("totalPages") int totalPages,
                        @JsonProperty("sort") Object sort,
                        @JsonProperty("first") boolean first,
                        @JsonProperty("empty") boolean empty,
                        @JsonProperty("numberOfElements") int numberOfElements
        ) {
            super(
                    content,
                    PageRequest.of(number, size, Sort.by("name","asc")),
                    totalElements
            );
        }

        public TestPage(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }

        public TestPage(List<T> content) {
            super(content);
        }

        public TestPage() {
            super(List.of());
        }
    }
}