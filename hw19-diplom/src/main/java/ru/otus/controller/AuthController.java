package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.model.AuthDto;
import ru.otus.model.LoginRequestDto;
import ru.otus.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthDto login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @GetMapping("/encode")
    public ResponseEntity<String> getEncoded(@RequestParam(name = "pass") String pass) {
        return ResponseEntity.ok(authService.encodePassword(pass));
    }

}