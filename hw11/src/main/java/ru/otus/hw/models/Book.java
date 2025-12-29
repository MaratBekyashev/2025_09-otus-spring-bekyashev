package ru.otus.hw.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @Column("id")
    private Long id;

    @Column("title")
    private String title;

    @Column("author_id")
    private Long authorId;

    @Column("genre_id")
    private Long genreId;

    @PersistenceCreator
    public Book(Long id, String title, Long authorId, Long genreId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
    }
}
