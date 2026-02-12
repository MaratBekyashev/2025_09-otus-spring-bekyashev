package ru.otus.hw.data;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentTestData {

    private static final List<Book> DB_BOOKS = BookTestData.getDbBooks();

    public static Map<Long, List<Comment>> getDbMapBooksComments() {
        Map<Long, List<Comment>> dbMapBooksComments = new HashMap<>();
        Book book1 = DB_BOOKS.get(0);
        dbMapBooksComments.put(book1.getId(), List.of(
                new Comment(1L, "comment_1", book1),
                new Comment(2L, "comment_2", book1),
                new Comment(3L, "comment_3", book1)));
        Book book2 = DB_BOOKS.get(1);
        dbMapBooksComments.put(book2.getId(), List.of(
                         new Comment(4L, "comment_4", book2),
                         new Comment(5L, "comment_5", book2),
                         new Comment(6L, "comment_6", book2)));
        Book book3 = DB_BOOKS.get(2);
        dbMapBooksComments.put(book3.getId(), List.of());
        return dbMapBooksComments;
    }

    public static Comment getNewComment() {
        return new Comment(7L, "new_comment", DB_BOOKS.get(2));
    }

    public static Comment getChangeComment() {
        return new Comment(1L, "change_comment", DB_BOOKS.get(2));
    }

    public static List<Comment> getDbComments() {
        return getDbMapBooksComments().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
