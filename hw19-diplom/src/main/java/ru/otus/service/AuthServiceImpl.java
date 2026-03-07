package ru.otus.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.entity.Role;
import ru.otus.entity.RoleNameEnum;
import ru.otus.entity.User;
import ru.otus.model.LoginRequestDto;
import ru.otus.model.RegisterRequestDto;
import ru.otus.repository.RoleRepository;
import ru.otus.repository.UserRepository;
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
/*        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
*/
        User user = userRepository
                .findByUserName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User '%s' not found".formatted(request.getUsername())));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
       //SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = jwtUtil.generateToken(request.getUsername());
        return token;
    }

    @Override
    @Transactional
    public void registerNewUser(RegisterRequestDto newUser) {
        if (userRepository.existsByUserNameIgnoreCase(newUser.getUsername())) {
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
        //String token = jwtUtil.generateToken(user.getUserName());
        //return token;
    }

    @Override
    public String encodePassword(String unencodedPassword) {
        return passwordEncoder.encode(unencodedPassword);
    }

}
