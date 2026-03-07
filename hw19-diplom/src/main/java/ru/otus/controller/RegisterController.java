package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.model.LoginRequestDto;
import ru.otus.model.RegisterRequestDto;
import ru.otus.service.AuthService;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class RegisterController {

    private final AuthService authService;

    @PostMapping("/users")
    public void register(@RequestBody RegisterRequestDto user) {
        authService.registerNewUser(user);
    }

}