package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.CreateMovieDto;
import org.javaacademy.cinema.dto.MovieDto;
import org.javaacademy.cinema.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(CreateMovieDto dto) {
        return new Movie(
                dto.getName(),
                dto.getDescription()
        );
    }

    public MovieDto toDto(Movie entity) {
        return MovieDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
