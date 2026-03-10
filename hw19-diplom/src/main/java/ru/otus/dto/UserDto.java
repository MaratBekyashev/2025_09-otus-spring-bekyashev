package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {

    private Long userId;

    private String userName;

    private String email;

    public static User toDomain (UserDto userDto) {
       return User.builder()
               .userId(userDto.getUserId())
               .userName(userDto.getUserName())
               .email(userDto.getEmail())
               .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(builder().email)
                .build();
    }
}
