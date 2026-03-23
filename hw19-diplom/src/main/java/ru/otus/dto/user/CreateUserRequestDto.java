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

    @NotBlank (message = "Логин пользователя должен быть указан ")
    private String login;

    private String password;

    private String fullUserName;

    private String email;

    private List<String> roles;

}
