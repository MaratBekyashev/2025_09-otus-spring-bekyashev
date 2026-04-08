package ru.otus.service;

import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;
import ru.otus.dto.user.UpdateUserRequestDto;
import ru.otus.model.users.UserSearchFilter;
import java.util.List;

public interface UserService {

    List<UserDto> search (UserSearchFilter filter);

    List<UserDto> getAllUsers();

    UserDto findUserById(Long userId);

    UserDto createUser(CreateUserRequestDto request);

    UserDto updateUser(Long userId, UpdateUserRequestDto request);

    void deleteUserById(Long userId);
}
