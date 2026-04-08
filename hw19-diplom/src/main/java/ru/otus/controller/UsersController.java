package ru.otus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;
import ru.otus.dto.user.UpdateUserRequestDto;
import ru.otus.model.users.UserSearchFilter;
import ru.otus.service.UserService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
@Slf4j
public class UsersController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String login,
                                                     @RequestParam(required = false) String userName,
                                                     @RequestParam(required = false) String email) {
        log.info("method searchUsers called. login={}, userName={},email={}", login, userName, email);
        var filter = new UserSearchFilter(login, userName, email);
        var response = userService.search(filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("method getAllUsers called");
        var response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        log.info("method getUser called. userId={}", userId);
        var response = userService.findUserById(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<UserDto> createUser (@RequestBody @Valid CreateUserRequestDto request) {
        log.info("method createUser called. params={}", request);
        var response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserDto> updateUser (@PathVariable Long userId,
                        @RequestBody UpdateUserRequestDto request) {
        log.info("method updateUser called. userId={}, params={}", userId, request);
        var response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    void deleteUser (@PathVariable Long userId) {
        log.info("method deleteUser called. userId={}", userId);
        userService.deleteUserById(userId);
    }

}
