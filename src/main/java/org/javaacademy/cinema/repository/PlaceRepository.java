package org.javaacademy.cinema.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.config.PlaceConfiguration;
import org.javaacademy.cinema.entity.Place;
import org.javaacademy.cinema.exception.DataMappingException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PlaceRepository {
    private static final String FIND_PLACE_BY_ID_SQL = "select * from place where id = ?";
    private static final String FIND_ALL_PLACE_SQL = "select * from place";
    private static final String PLACE_COUNT_SQL = "select count(*) from place";
    private static final String SAVE_PLACE_SQL = "insert into place (name) values (?)";
    private static final String PLACE_NUMBER_FORMAT = "%s%s";
    private final JdbcTemplate jdbcTemplate;
    private final PlaceConfiguration placeConfiguration;

    @PostConstruct
    public void initPlace() {
        Integer count = jdbcTemplate.queryForObject(PLACE_COUNT_SQL, Integer.class);
        if (count == 0) {
            createPlace(
                    placeConfiguration.getStartRow(),
                    placeConfiguration.getEndRow(),
                    placeConfiguration.getMaxSeatsPerRow()
            );
        }
    }

    private void createPlace(char startRow, char endRow, int maxNumber) {
        for (char row = startRow; row <= endRow; row++) {
            for (int number = 1; number <= maxNumber; number++) {
                jdbcTemplate.update(SAVE_PLACE_SQL, PLACE_NUMBER_FORMAT.formatted(row, number));
            }
        }
    }

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
