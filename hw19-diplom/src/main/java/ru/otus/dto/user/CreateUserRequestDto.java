package ru.otus.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotBlank (message = "Логин пользователя должен быть указан")
    private String login;

    @NotBlank (message = "Пароль пользователя должен быть указан")
    private String password;

    @NotBlank (message = "ФИО пользователя должно быть указано")
    private String fullUserName;

    private String email;

    private List<String> roles;

}
