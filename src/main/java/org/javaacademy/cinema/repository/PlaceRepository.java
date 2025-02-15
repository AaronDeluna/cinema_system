package org.javaacademy.cinema.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.entity.Place;
import org.javaacademy.cinema.exception.DataMappingException;
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
public class PlaceRepository {
    private static final String FIND_PLACE_BY_ID_SQL = "select * from place where id = ?";
    private static final String FIND_ALL_PLACE_SQL = "select * from place";
    private static final String PLACE_COUNT_SQL = "select count(*) from place";
    private static final String SAVE_PLACE_SQL = "insert into place (name) values (?)";
    private final JdbcTemplate jdbcTemplate;

    public boolean isPlacesAbsent() {
        return jdbcTemplate.queryForObject(PLACE_COUNT_SQL, Integer.class) == 0;
    }

    public void createPlace(String place) {
        jdbcTemplate.update(SAVE_PLACE_SQL, place);
    }

    public Optional<Place> findById(int id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_PLACE_BY_ID_SQL, this::toPlace, id));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Произошла ошибка при поиске по id {}, {}", id, e.getMessage());
            return empty();
        }
    }

    public List<Place> findAll() {
        try {
            return jdbcTemplate.query(FIND_ALL_PLACE_SQL, this::toPlace);
        } catch (DataAccessException e) {
            log.warn("Произошла ошибка при получении всех мест {}", e.getMessage());
            return emptyList();
        }
    }

    private Place toPlace(ResultSet rs, int rowNum) {
        try {
            Place place = new Place();
            place.setId(rs.getInt("id"));
            place.setName(rs.getString("name"));
            return place;
        } catch (SQLException e) {
            throw new DataMappingException(e);
        }
    }
}
