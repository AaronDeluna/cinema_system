package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.javaacademy.cinema.mapper.MovieMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieDto create(CreateMovieDto createMovieDto) {
        Movie movie = movieRepository.save(movieMapper.toEntity(createMovieDto));
        return movieMapper.toDto(movie);
    }

    public List<ResponseMovieDto> findAll() {
        return movieMapper.toDtos(movieRepository.findAll());
    }
}
