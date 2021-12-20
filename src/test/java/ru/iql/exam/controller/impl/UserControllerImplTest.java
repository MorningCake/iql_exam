package ru.iql.exam.controller.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.iql.exam.common.UserData;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.mapping.dto.UserDto;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.mapping.mapper.UserMapper;
import ru.iql.exam.model.User;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.iql.exam.constant.ComparisonType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Тест контроллера пользователей")
class UserControllerImplTest {

    @Autowired private UserRepository userRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserMapper userMapper;

    private static final String SEARCH_URI = "/api/users/search/filters";
    private static final String INCORRECT_URI = "/incorrect/uri";

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    @BeforeEach
    void init() {
        user1 = UserData.createUser(100_00, Set.of("+79270000001"), 20, "User1", "email1@email.ru");
        user2 = UserData.createUser(220_00, Set.of("+79220000001"), 18, "User2", "email2@email.ru");
        user3 = UserData.createUser(0,      Set.of("+79250000002"), 20, "User3", "email3@email.ru");
        user4 = UserData.createUser(300_00, Set.of("+79270000002"), 20, "User4", "email4@gmail.ru");
        user5 = UserData.createUser(500_00, Set.of("+79660000001"), 20, "User5", "email5@email.ru");
        user6 = UserData.createUser(20_00,  Set.of("+79290000003"), 30, "Admin", "email6@email.ru");

        userRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6));
        assertThat(userRepository.count()).isEqualTo(6);
    }

    @AfterEach
    void clearing() {
        userRepository.deleteAll();
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Тест фильтров 1 - по всем полям - успех")
    void searchWithFilters_allFields_success() throws Exception {
        UserSearch searchParams = UserData.createAllFilters(
                20, GREATER_THAN_OR_EQUAL_TO, 50_00, GREATER_THAN,
                "User", "@email", "+792",
                0, 10);

        TestPage<UserDto> actualPage = createPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        // выборочная проверка
        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user1.getName());
        assertThat(actual.getPhones().get(0).getValue()).isEqualTo(user1.getPhones().get(0).getValue());
        assertThat(actual.getProfile().getCash()).isEqualTo(user1.getProfile().getCash());
    }

    @Test
    @DisplayName("Тест фильтров 2 - выборочно (возраст и баланс) - успех")
    void searchWithFilters_byAgeAndCash_success() throws Exception {
        UserSearch searchParams = UserData.createNumericFilters(
                50, LESS_THAN_OR_EQUAL_TO, 10_00, LESS_THAN,
                0, 10);

        TestPage<UserDto> actualPage = createPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user3.getName());
        assertThat(actual.getAge()).isEqualTo(user3.getAge());
        assertThat(actual.getProfile().getCash()).isEqualTo(user3.getProfile().getCash());
    }

    @Test
    @DisplayName("Тест фильтров 3 - выборочно (текстовый) - успех")
    void searchWithFilters_textFields_success() throws Exception {
        UserSearch searchParams = UserData.createTextFilters(
                "User", "@gmail", "+7",
                0, 5);

        TestPage<UserDto> actualPage = createPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(1);

        UserDto actual = actualPage.getContent().get(0);
        assertThat(actual.getName()).isEqualTo(user4.getName());
    }

    @Test
    @DisplayName("Тест фильтров 4 - выборочно (возврат списка) - успех")
    void searchWithFilters_textFields_success_resultList() throws Exception {
        UserSearch searchParams = UserData.createTextFilters(
                "User", "mail", "+7",
                0, 7);

        TestPage<UserDto> actualPage = createPostRequest(SEARCH_URI, searchParams);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(5);
    }

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

    @Test
    @DisplayName("Тест фильтров 6 - некорректный uri - исключение")
    void searchWithFilters_incorrectUri_ex() throws Exception {
        UserSearch searchParams = UserData.createAllFilters(
                20, GREATER_THAN_OR_EQUAL_TO, 50_00, GREATER_THAN,
                "User", "mail", "+7",
                0, 10);

        Exception ex = createIncorrectPostRequest(INCORRECT_URI, status().isNotFound(), searchParams);
    }

    /**
     * Создать POST запрос
     * @param searchParams UserSearch
     * @param uri API uri
     * @return TestPage<UserDto>
     * @throws Exception
     */
    private TestPage<UserDto> createPostRequest(String uri, UserSearch searchParams) throws Exception {
        var request = objectMapper.writeValueAsString(searchParams);

        var response = mockMvc.perform(
                post(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readValue(
                response.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {});
    }

    /**
     * Создать некорректный POST запрос
     * @param uri API uri
     * @param status ResultMatcher
     * @param searchParams UserSearch
     * @return Exception
     * @throws Exception
     */
    private Exception createIncorrectPostRequest(String uri, ResultMatcher status, UserSearch searchParams)
            throws Exception {
        var request = objectMapper.writeValueAsString(searchParams);

        return mockMvc.perform(
                post(uri).content(request).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status).andReturn().getResolvedException();
    }

    @Test
    void create() {
    }

    @Test
    void updateSelf() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }


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