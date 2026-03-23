package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.User;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {

    private Long userId;

    private String login;

    private String userName;

    private String email;

    public static User toDomain (UserDto userDto) {
       return User.builder()
               .userId(userDto.getUserId())
               .login(userDto.getLogin())
               .userName(userDto.getUserName())
               .email(userDto.getEmail())
               .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .login(user.getLogin())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserDto::toDto).toList();
    }
}
