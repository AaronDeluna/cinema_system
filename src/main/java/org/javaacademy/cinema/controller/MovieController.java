package org.javaacademy.cinema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.ErrorResponse;
import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.service.MovieService;
import org.javaacademy.cinema.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final AuthorizationService authorizationService;

    @Operation(
            summary = "Сохранение фильма"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Успешно создано",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<MovieDto> create(@RequestHeader("user-token") String token,
                                           @RequestBody @Validated CreateMovieDto createMovieDto) {
        authorizationService.tokenValidation(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(createMovieDto));
    }

    @Operation(
            summary = "Полученеи всех доступных фильмов"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ResponseMovieDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<ResponseMovieDto>> getAll() {
        return ResponseEntity.ok().body(movieService.findAll());
    }
}
