package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.User;
import ru.otus.model.IdentifableEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto implements IdentifableEntity {

    private Long userId;

    private String login;

    private String userName;

    private String email;

    private Set<String> roles;

    public static User toDomain (UserDto userDto) {
       return User.builder()
               .userId(userDto.getUserId())
               .login(userDto.getLogin())
               .userName(userDto.getUserName())
               .email(userDto.getEmail())
               .build();
    }

    public static UserDto toDto(User user) {
        var roles = user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .collect(Collectors.toSet());
        return UserDto.builder()
                .userId(user.getUserId())
                .login(user.getLogin())
                .userName(user.getUserName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserDto::toDto).toList();
    }

    @Override
    public Long getId() {
        return this.userId;
    }
}
