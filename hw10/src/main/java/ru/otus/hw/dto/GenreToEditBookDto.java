package ru.otus.hw.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import ru.otus.hw.models.Genre;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreToEditBookDto {

    private Long id;

    private String name;

    private boolean picked;

    /*public static List<GenreToEditBookDto> toDtoList(List<Genre> allGenres,
                                                     Long currentBookGenreId) {

        List<Long> allGenresIds = Optional.ofNullable(allGenres)
                .stream()
                .flatMap(List::stream)
                .map(Genre::getId).toList();
        return allGenres.stream()
                .map(g -> new GenreToEditBookDto(g.getId(), g.getName(), allGenresIds.contains(currentBookGenreId)))
                .toList();
    }*/
}
