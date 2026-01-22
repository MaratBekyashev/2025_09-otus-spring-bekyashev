package ru.otus.hw.modelMongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
@ToString
public class BookDocument {

    @Id
    private String id;

    private String title;

    private AuthorDocument author;

    private GenreDocument genre;

    private List<CommentDocument> comments;
}
