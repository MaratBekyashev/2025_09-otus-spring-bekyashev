package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.entity.User;
import ru.otus.repository.UserRepository;
import ru.otus.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService, EvictCacheService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "usersCache", key = "#userLogin")
    public UserDetails loadUserByUsername(String userLogin) throws UsernameNotFoundException {
        log.info("Users cache for user (login={}) has been filled", userLogin);
        User user = userRepository
                .findByLogin(userLogin)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }

    @Override
    @CacheEvict(value = "usersCache", key = "#userLogin")
    public void evictUsersCache(String userLogin) {
        log.info("Users cache for user(login={}) has been invalidated", userLogin);
    }
}