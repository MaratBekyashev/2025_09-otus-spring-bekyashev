package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        var resultList = namedParameterJdbcOperations.query("select * from authors", new AuthorRowMapper());
        return resultList;
    }

    @Override
    public Optional<Author> findById(long id) {
        Map<String, Object> queryParams = Map.of("p_id", id);
        try {
            Author result = namedParameterJdbcOperations.queryForObject("select * from authors where id=:p_id",
                        queryParams,
                        new AuthorRowMapper());
             return Optional.of(result);
        }
        catch (EmptyResultDataAccessException ex ) {
            return Optional.empty();
        }
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long authorId = rs.getLong("id");
            String fn = rs.getString("full_name" );
            return new Author(authorId, fn);
        }
    }
}
