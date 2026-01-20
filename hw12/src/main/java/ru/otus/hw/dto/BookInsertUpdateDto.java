package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Genre;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookInsertUpdateDto {

    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private Long authorId;

    @NotNull
    private Long genreId;

    public static BookInsertUpdateDto toDto(BookDto bookDto) {
        return new BookInsertUpdateDto(bookDto.getId(), bookDto.getTitle(), bookDto.getAuthor().getId(), bookDto.getGenre().getId());
    }
}
