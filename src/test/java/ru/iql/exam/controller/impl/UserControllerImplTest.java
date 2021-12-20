package ru.iql.exam.controller.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.iql.exam.constant.ComparisonType;
import ru.iql.exam.dao.UserRepository;
import ru.iql.exam.mapping.dto.AgeFilter;
import ru.iql.exam.mapping.dto.CashFilter;
import ru.iql.exam.mapping.dto.UserDto;
import ru.iql.exam.mapping.dto.UserSearch;
import ru.iql.exam.mapping.mapper.UserMapper;
import ru.iql.exam.model.User;
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.model.UserProfile;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerImplTest {

    @Autowired private UserRepository userRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserMapper userMapper;

    // todo нормальные тесты - общие классы для выборки, потестить фильтры

    @Test
    void searchWithFilters() throws Exception {
        UserProfile profile1 = UserProfile.builder().cash(100_00).build();
        UserPhone phone1_1 = UserPhone.builder().value("+79270000001").build();
        UserPhone phone1_2 = UserPhone.builder().value("+79030000001").build();
        List<UserPhone> phones1 = List.of(phone1_1, phone1_2);
        User user1 = User.builder()
                .age(20)
                .name("user1")
                .email("email1@email.ru")
                .profile(profile1)
                .phones(phones1)
                .build();

        user1 = userRepository.save(user1);
        assertThat(user1.getId()).isNotNull();

        AgeFilter ageFilter = AgeFilter.builder().age(18).comparisonType(ComparisonType.GREATER_THAN).build();
        CashFilter cashFilter = CashFilter.builder().cash(10000_00).comparisonType(ComparisonType.LESS_THAN).build();

        UserSearch searchParams = UserSearch
                .builder()
                .ageFilter(ageFilter)
                .cashFilter(cashFilter)
                .email("mail")
                .name("user")
                .phone("+7")
                .pageNumber(0)
                .pageSize(10)
                .build();

        var request = objectMapper.writeValueAsString(searchParams);

        var response = mockMvc.perform(
                post("/api/users/search/filters")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();

        TestPage<UserDto> actual = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {});

        assertThat(actual).isNotNull();

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