package ru.otus.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.dto.UserDto;
import ru.otus.entity.Role;
import ru.otus.entity.RoleNameEnum;
import ru.otus.entity.User;
import ru.otus.model.LoginRequestDto;
import ru.otus.model.RegisterRequestDto;
import ru.otus.repository.RoleRepository;
import ru.otus.repository.UserRepository;
import ru.otus.security.CustomUserDetails;
import ru.otus.util.JwtUtil;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    //private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    @Override
    public String login(LoginRequestDto request) {
        User user = userRepository
                .findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("User '%s' not found".formatted(request.getLogin())));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = jwtUtil.generateToken(request.getLogin());
        return token;
    }

    @Override
    @Transactional
    public void registerNewUser(RegisterRequestDto newUser) {
        if (userRepository.existsByLoginIgnoreCase(newUser.getUsername())) {
            throw new RuntimeException("User already exists");
        }
        Role userRole = roleRepository
                .findByRoleName(RoleNameEnum.ROLE_USER)
                .orElseThrow();
        User user = User.builder()
                .userName(newUser.getUsername())
                .email(newUser.getEmail())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
    }

    @Override
    public String encodePassword(String unencodedPassword) {
        return passwordEncoder.encode(unencodedPassword);
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        var user = userDetails.getUser();
        return UserDto.toDto(user);
    }

}
