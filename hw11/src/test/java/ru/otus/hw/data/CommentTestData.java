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
                new Comment(1L, "comment_1", book1.getId()),
                new Comment(2L, "comment_2", book1.getId()),
                new Comment(3L, "comment_3", book1.getId())));
        Book book2 = DB_BOOKS.get(1);
        dbMapBooksComments.put(book2.getId(), List.of(
                         new Comment(4L, "comment_4", book2.getId()),
                         new Comment(5L, "comment_5", book2.getId()),
                         new Comment(6L, "comment_6", book2.getId())));
        Book book3 = DB_BOOKS.get(2);
        dbMapBooksComments.put(book3.getId(), List.of(
                new Comment(7L, "comment_7", book3.getId()),
                new Comment(8L, "comment_8", book3.getId()),
                new Comment(9L, "comment_9", book3.getId())));

        return dbMapBooksComments;
    }

    public static Comment getNewComment() {
        return new Comment(7L, "new_comment", DB_BOOKS.get(2).getId());
    }

    public static Comment getChangeComment() {
        return new Comment(1L, "change_comment", DB_BOOKS.get(2).getId());
    }

    public static List<Comment> getDbComments() {
        return getDbMapBooksComments().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
