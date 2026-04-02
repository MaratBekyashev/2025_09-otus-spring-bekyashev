package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.annotation.Auditable;
import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;
import ru.otus.dto.user.UpdateUserRequestDto;
import ru.otus.entity.Role;
import ru.otus.entity.User;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.exception.UserAlreadyExistException;
import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import ru.otus.model.users.UserSearchFilter;
import ru.otus.model.users.UserSearchSpecification;
import ru.otus.repository.RoleRepository;
import ru.otus.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final PasswordEncoder passEncoder;

    private final RoleRepository roleRepo;

    private final EvictCacheService evictCacheService;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> search(UserSearchFilter filter) {
        Specification<User> spec = UserSearchSpecification.build(filter);
        List<User> dataList = userRepo.findAll(spec);
        return UserDto.toDtoList(dataList);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> dataList = userRepo.getAllUsers();
        var resultList = UserDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserById(Long userId) {
        User user = findUser(userId)
                      .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(userId)));;
        return UserDto.toDto(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(entity = AuditEntityTypeEnum.USER, action = AuditActionEnum.CREATED)
    public UserDto createUser(CreateUserRequestDto request) {
        String login = request.getLogin().trim();
        if (userRepo.existsByLoginIgnoreCaseAndIsDeletedIsNull(login)) {
            throw new UserAlreadyExistException("User already exists(login=%s)".formatted(login));
        }
        String encodedPass = passEncoder.encode(request.getPassword().trim());
        User user = User.builder()
                .login(login)
                .password(encodedPass)
                .userName(request.getFullUserName().trim())
                .email(request.getEmail().trim())
                .build();

        if(request.getRoles()!= null) {
            List<Role> roles = roleRepo.getAllByRoleNames(request.getRoles());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }

        var saved = userRepo.save(user);
        return UserDto.toDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(entity = AuditEntityTypeEnum.USER, action = AuditActionEnum.EDITED)
    public UserDto updateUser(Long userId, UpdateUserRequestDto request) {
        User user = findUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(userId)));

        var oldLogin = user.getLogin();
        var newLogin = request.getLogin().trim();
        if (!Objects.equals (newLogin, oldLogin)) {
            if (userRepo.existsByLoginIgnoreCaseAndIsDeletedIsNull(newLogin)) {
                throw new UserAlreadyExistException("User already exists(login=%s)".formatted(newLogin));
            }
            user.setLogin(newLogin);
        }
        if (!Objects.equals(request.getPassword(), user.getPassword())) {
            String encodedPass = passEncoder.encode(request.getPassword().trim());
            user.setPassword(encodedPass);
        }
        if (!Objects.equals(request.getFullUserName(), user.getUserName())) {
            user.setUserName(request.getFullUserName().trim());
        }
        if (!Objects.equals(request.getEmail(), user.getEmail())) {
            user.setEmail(request.getEmail().trim());
        }
        if(request.getRoles()!= null) {
            List<Role> roles = roleRepo.getAllByRoleNames(request.getRoles());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        var saved = userRepo.save(user);

        evictCacheService.evictUsersCache(oldLogin);

        return UserDto.toDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(entity = AuditEntityTypeEnum.USER, action = AuditActionEnum.DELETED, idFieldName = "userId")
    public void deleteUserById(Long userId) {
        User user = findUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(userId)));
        user.setIsDeleted("Y");
        userRepo.save(user);
    }

    private Optional<User> findUser(Long userId ) {
        Optional<User> user = userRepo.findById(userId);
        return user;
    }

}
