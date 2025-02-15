package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.exception.DataMappingException;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MovieRepository {
    private static final String MOVIE_SAVE_ERROR_MESSAGE = "Не удалось сохранить фильм: %s";
    private static final String FIND_BY_ID_SQL = "select * from movie where id = ?";
    private static final String FIND_ALL_SQL = "select * from movie";
    private static final String SAVE_MOVIE_SQL = "insert into movie (name, description) values (?, ?) RETURNING id";
    private final JdbcTemplate jdbcTemplate;

    public Movie save(Movie movie) {
        try {
            Integer movieId = jdbcTemplate.queryForObject(
                    SAVE_MOVIE_SQL,
                    Integer.class,
                    movie.getName(),
                    movie.getDescription()
            );
            movie.setId(movieId);
            return movie;
        } catch (DataAccessException e) {
            throw new EntitySaveException(MOVIE_SAVE_ERROR_MESSAGE, e);
        }
    }

    public List<Movie> findAll() {
        try {
            return jdbcTemplate.query(FIND_ALL_SQL, this::toMove);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске всех фильмов: {}", e.getMessage());
            return emptyList();
        }
    }

    public Optional<Movie> findById(int id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_ID_SQL, this::toMove, id));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Произошла ошибка при поиске по id {}, {}", id, e.getMessage());
            return empty();
        }
    }

    private Movie toMove(ResultSet rs, int rowNum) {
        try {
            Movie movie = new Movie();
            movie.setId(rs.getInt("id"));
            movie.setName(rs.getString("name"));
            movie.setDescription(rs.getString("description"));
            return movie;
        } catch (SQLException e) {
            throw new DataMappingException(e);
        }
    }
}
