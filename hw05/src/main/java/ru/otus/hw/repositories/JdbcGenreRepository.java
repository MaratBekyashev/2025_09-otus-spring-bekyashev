package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        List<Genre> resultList = namedParameterJdbcOperations.query("select id, name from genres",
                new GnreRowMapper());
        return resultList;
    }

    @Override
    public Optional<Genre> findById(long id) {
        Map<String, Object> params = Map.of("id", id);
        try {
            Genre result = namedParameterJdbcOperations.queryForObject("select id, name from genres where id = :id",
                    params,
                    new GnreRowMapper());
            return Optional.of(result);
        }
        catch (EmptyResultDataAccessException ex ) {
            return Optional.empty();
        }
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
