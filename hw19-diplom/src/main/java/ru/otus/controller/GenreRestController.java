package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping("/api/genres")
    public List<Genre> getListGenres() {
        return genreService.findAll();
    }
}