package ru.otus.model.users;

public record UserSearchFilter(

        String login,
        String userName,
        String email
) {}