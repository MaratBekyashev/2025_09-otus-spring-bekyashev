package ru.otus.service;

import ru.otus.model.LoginRequestDto;
import ru.otus.model.RegisterRequestDto;

public interface AuthService {

    String login(LoginRequestDto request);

    void registerNewUser(RegisterRequestDto newUser);

    String encodePassword (String unencodedPassword);
}
