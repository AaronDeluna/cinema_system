package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.session.CreateSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.javaacademy.cinema.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private static final String ID_NOT_FOUND_MESSAGE = "Ошибка: id: '%s', не найден";
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;

    public SessionDto create(CreateSessionDto createSessionDto) {
        Movie movie = movieRepository.findById(createSessionDto.getMovieId())
                .orElseThrow(
                        () -> new NotFoundException(ID_NOT_FOUND_MESSAGE.formatted(createSessionDto.getMovieId()))
                );
        SessionDto sessionDto = buildSessionDto(createSessionDto, movie);
        SessionDto responseSession = saveSession(sessionDto);
        ticketService.create(responseSession);
        return responseSession;
    }

    private SessionDto buildSessionDto(CreateSessionDto createSessionDto, Movie movie) {
        return SessionDto.builder()
                .movie(movie)
                .datetime(createSessionDto.getDatetime())
                .price(createSessionDto.getPrice())
                .build();
    }

    private SessionDto saveSession(SessionDto sessionDto) {
        return sessionMapper.toDto(
                sessionRepository.save(sessionMapper.toEntity(sessionDto)).orElseThrow()
        );
    }
}
