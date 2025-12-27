package ru.otus.hw.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

@Getter
@Setter
@NoArgsConstructor
public class UpdateBookDto {

    private Long id;

    private String title;

    private Author author;

    private Genre genre;

}
