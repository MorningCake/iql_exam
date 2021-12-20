package ru.iql.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.iql.exam.mapping.dto.*;

import javax.validation.Valid;

//todo ролевка

@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Работа с пользователями")
public interface UserController {

    @PostMapping(path = "/search/filters", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Поиск", description = "Поиск пользователей по фильтру")
    @ResponseBody
    Page<UserDto> searchWithFilters(
            @RequestBody @Valid UserSearch filters
    );

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Создание", description = "Создание нового пользователя")
    @ResponseBody
    UserDto create(@RequestBody @Valid NewUserDto dto);

    @PutMapping(path = "/self/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Редактирование собственных данных", description = "Редактирование собственных данных")
    void updateSelf(@RequestBody @Valid SelfUpdateUserDto dto, @PathVariable("id") Long id);
//todo                    @Parameter(hidden = true) Authentication authentication,
//                    @Parameter(hidden = true) @RequestHeader("Authorization") String token);

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Редактирование", description = "Редактирование данных пользователя")
    void update(@RequestBody @Valid NewUserDto dto, @PathVariable("id") Long id);

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удаление", description = "Удаление данных пользователя (soft)")
    void delete(@PathVariable("id") Long id);
}
