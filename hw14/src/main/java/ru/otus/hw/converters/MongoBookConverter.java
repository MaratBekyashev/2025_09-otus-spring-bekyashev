package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.modelMongo.BookDocument;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
public class MongoBookConverter {

    public static String bookToString(BookDocument book) {
        return "%s".formatted(book.toString());
    }
}
