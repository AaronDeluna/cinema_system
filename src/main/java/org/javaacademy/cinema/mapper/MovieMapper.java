package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public ResponseMovieDto toResponseDto(Movie movie) {
        return ResponseMovieDto.builder()
                .name(movie.getName())
                .description(movie.getDescription())
                .build();
    }

    public List<ResponseMovieDto> toDtos(List<Movie> movies) {
        return movies.stream()
                .map(this::toResponseDto)
                .toList();
    }
}
