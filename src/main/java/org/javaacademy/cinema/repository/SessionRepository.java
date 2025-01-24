package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.entity.Session;
import org.javaacademy.cinema.util.DbUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SessionRepository {
    private static final String FIND_BY_ID_SQL = "select * from session where id = ?";
    private static final String FIND_ALL_SQL = "select * from session";
    private static final String SAVE_SESSION_SQL = "insert into session (movie_id, price, datetime) values(?, ?, ?) returning id";
    private final JdbcTemplate jdbcTemplate;
    private final MovieRepository movieRepository;

    public Optional<Session> save(Session session) {
        try {
            Integer sessionId = jdbcTemplate.queryForObject(
                    SAVE_SESSION_SQL,
                    new Object[] {session.getMovie(), session.getPrice(), session.getDatetime()},
                    Integer.class
            );
            session.setId(sessionId);
            return Optional.of(session);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при сохранении: {}", e.getMessage());
            return empty();
        }
    }

    public Optional<Session> findById(int id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_ID_SQL, this::toSession, id));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Произошла ошибка при поиске по id {}, {}", id, e.getMessage());
            return empty();
        }
    }

    public Optional<List<Session>> findAll() {
        try {
            List<Session> sessions = jdbcTemplate.query(FIND_ALL_SQL, this::toSession);
            return Optional.of(sessions);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при поиске всех сеансов: {}", e.getMessage());
            return empty();
        }
    }

    @SneakyThrows
    private Session toSession(ResultSet rs, int rowNum) {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        session.setMovie(DbUtils.getEntityById(rs.getString("movie_id"), movieRepository::findById));
        session.setDatetime(rs.getTimestamp("datetime").toLocalDateTime());
        session.setPrice(rs.getBigDecimal("price"));
        return session;
    }

}
