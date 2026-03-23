package ru.otus.service;

import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto createUser(CreateUserRequestDto request);
}
