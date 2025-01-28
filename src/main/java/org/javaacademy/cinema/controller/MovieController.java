package org.javaacademy.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieDto> create(@RequestBody @Validated CreateMovieDto createMovieDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(createMovieDto));
    }

    @GetMapping
    public ResponseEntity<List<ResponseMovieDto>> getAll() {
        return ResponseEntity.ok().body(movieService.findAll());
    }
}
