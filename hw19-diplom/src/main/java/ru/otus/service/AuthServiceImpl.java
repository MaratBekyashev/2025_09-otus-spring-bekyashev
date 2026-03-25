package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.dto.UserDto;
import ru.otus.model.AuthDto;
import ru.otus.model.LoginRequestDto;
import ru.otus.security.CustomUserDetails;
import ru.otus.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final EvictCacheService evictCacheService;

    private final JwtUtil jwtUtil;

    @Override
    public AuthDto login(LoginRequestDto request) {
        String login = request.getLogin().trim();

        UserDetails user = userDetailsService.loadUserByUsername(login);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var token = jwtUtil.generateToken(login);
        var authenticationDto = new AuthDto(token);
        evictCacheService.evictUsersCache(login);
        return authenticationDto;
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
