package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Place;
import org.javaacademy.cinema.entity.Session;
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
public class PlaceRepository {
    private static final String FIND_PLACE_BY_ID_SQL = "select * from place where id = ?";
    private static final String FIND_ALL_PLACE_SQL = "select * from place";
    private final JdbcTemplate jdbcTemplate;

    public Optional<Place> findById(int id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_PLACE_BY_ID_SQL, this::toPlace, id));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Произошла ошибка при поиске по id {}, {}", id, e.getMessage());
            return empty();
        }
    }

    public Optional<List<Place>> findAll() {
        try {
            List<Place> places = jdbcTemplate.query(FIND_ALL_PLACE_SQL, this::toPlace);
            return Optional.of(places);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при получении всех мест {}", e.getMessage());
            return empty();
        }
    }

    @SneakyThrows
    private Place toPlace(ResultSet rs, int rowNum) {
        Place place = new Place();
        place.setId(rs.getInt("id"));
        place.setName(rs.getString("name"));
        return place;
    }
}
