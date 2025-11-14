package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Map.of("id", id);
        String query =
                        "select b.id as book_id,"+
                        "       b.title as book_title,"+
                        "       a.id as author_id,"+
                        "       a.full_name as author_full_name,"+
                        "       g.id as genre_id,"+
                        "       g.name as genre_name"+
                        "  from books b "+
                        "  left join authors a"+
                        "    on b.author_id = a.id"+
                        "  left join genres g"+
                        "    on b.genre_id = g.id"+
                        " where b.id = :id";
        List<Book> books = namedParameterJdbcOperations.query(query, params, new BookResultSetExtractor());
        Optional<Book> result = books.stream().findFirst();
        return result;
    }

    @Override
    public List<Book> findAll() {
        String query =
                "select b.id as book_id,"+
                "       b.title as book_title,"+
                "       a.id as author_id,"+
                "       a.full_name as author_full_name,"+
                "       g.id as genre_id,"+
                "       g.name as genre_name"+
                " from books b "+
                "  left join authors a"+
                "    on b.author_id = a.id"+
                "  left join genres g"+
                "    on b.genre_id = g.id";
        List<Book> booksList = namedParameterJdbcOperations.query(query, new BookResultSetExtractor());
        return booksList;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Object> params = Map.of("id", id);
        int rowsDeleted = namedParameterJdbcOperations.update("delete from books where id = :id", params);
        if (rowsDeleted == 0) {
            throw new EntityNotFoundException("Book is not found (id=%d)".formatted(id));
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put("title", book.getTitle());
        params.put("authorId", book.getAuthor().getId());
        params.put("genre_id", book.getGenre().getId());
        namedParameterJdbcOperations.update(
                "insert into books(title, author_id, genre_id) values (:title, :authorId, :genre_id)",
                new MapSqlParameterSource(params), keyHolder, new String[]{"id"});

        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        params.put("authorId", book.getAuthor().getId());
        params.put("genre_id", book.getGenre().getId());
        int sqlRowCount = namedParameterJdbcOperations.update(
                "update books b set b.title = :title, b.author_id = :authorId, b.genre_id = :genre_id where id = :id",
                new MapSqlParameterSource(params), keyHolder, new String[]{"id"});
        if (sqlRowCount == 0){
            throw new EntityNotFoundException("Book not found (id = %d)".formatted(book.getId()));
        }
        return book;
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Book> resultList = new ArrayList<>();
            while (rs.next()) {
                long bookId = rs.getLong("book_id");
                String bookTitle = rs.getString("book_title");
                long authorId = rs.getLong("author_id");
                String authorFullName = rs.getString("author_full_name");
                long genreId =  rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");

                Book book = new Book(bookId, bookTitle, new Author(authorId, authorFullName), new Genre(genreId, genreName));
                resultList.add(book);
            }
            return resultList;
        }
    }

}