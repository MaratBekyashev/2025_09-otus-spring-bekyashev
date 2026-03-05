package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GenreController {

    @GetMapping("/library/genre")
    public String getListGenresPage() {
        return "genreList";
    }
}
