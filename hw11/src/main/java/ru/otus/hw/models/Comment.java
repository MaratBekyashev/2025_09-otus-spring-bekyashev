package ru.otus.hw.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "comments")
@ToString
@EqualsAndHashCode
public class Comment {

    @Id
    @Column("id")
    private Long id;

    @Column("content")
    private String content;

    @Column("book_id")
    private Long bookId;

    @PersistenceCreator
    public Comment(Long id, String content, Long bookId) {
        this.id = id;
        this.content = content;
        this.bookId = bookId;
    }
}
