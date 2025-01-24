package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.CreateMovieDto;
import org.javaacademy.cinema.dto.MovieDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.mapper.MovieMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieDto create(CreateMovieDto createMovieDto) {
        Movie movie = movieRepository.save(movieMapper.toEntity(createMovieDto)).orElseThrow();
        return movieMapper.toDto(movie);
    }
}
