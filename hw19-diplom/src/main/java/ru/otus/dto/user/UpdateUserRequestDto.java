package ru.otus.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {

    private String login;

    private String password;

    private String fullUserName;

    private String email;

    private List<String> roles;

}
