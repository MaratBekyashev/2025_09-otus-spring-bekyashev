package ru.otus.service;

import ru.otus.dto.UserDto;
import ru.otus.model.AuthDto;
import ru.otus.model.LoginRequestDto;

public interface AuthService {

    AuthDto login(LoginRequestDto request);

    String encodePassword (String unencodedPassword);

    UserDto getCurrentUser();
}
