package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;
import ru.otus.entity.User;
import ru.otus.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    @Override
    public List<UserDto> getAllUsers() {
        List<User> dataList = userRepo.getAllUsers();
        var resultList = UserDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    public UserDto createUser(CreateUserRequestDto request) {
        return null;
    }
}
