package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.mapper.MovieMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final String MOVIE_SAVE_ERROR_MESSAGE = "Не удалось сохранить фильм";
    private static final String MOVIE_NOT_FOUND_MESSAGE = "Фильмы не найдены";
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieDto create(CreateMovieDto createMovieDto) {
        Movie movie = movieRepository.save(movieMapper.toEntity(createMovieDto))
                .orElseThrow(() -> new EntitySaveException(MOVIE_SAVE_ERROR_MESSAGE));
        return movieMapper.toDto(movie);
    }

    public List<ResponseMovieDto> findAll() {
        return movieMapper.toDtos(movieRepository.findAll()
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND_MESSAGE))
        );
    }
}
