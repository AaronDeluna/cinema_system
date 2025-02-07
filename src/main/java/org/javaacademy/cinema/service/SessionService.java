package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.session.CreateSessionDto;
import org.javaacademy.cinema.dto.session.ResponseSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.javaacademy.cinema.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class SessionService {
    private static final String ID_NOT_FOUND_ERROR_MESSAGE = "Ошибка: id: '%s', не найден";
    private static final String SESSION_SAVE_ERROR_MESSAGE = "Не удалось сохранить сессию";
    private static final String FREE_PLACES_NOT_FOUND = "Свободные места не найдены для сеанса с id: %s";
    private static final boolean NOT_PAID_STATUS = false;
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;

    public SessionDto create(CreateSessionDto createSessionDto) {
        Movie movie = movieRepository.findById(createSessionDto.getMovieId())
                .orElseThrow(() -> new NotFoundException(
                                ID_NOT_FOUND_ERROR_MESSAGE.formatted(createSessionDto.getMovieId()))
                );
        SessionDto sessionDto = sessionMapper.toDto(createSessionDto, movie);
        SessionDto responseSession = saveSession(sessionDto);
        ticketService.create(responseSession);
        return responseSession;
    }

    public List<String> findAllFreePlaceById(int number) {
        List<TicketDto> ticketDtos = ticketService.findAllByPaidStatus(NOT_PAID_STATUS);

        List<String> freePlaces = ticketDtos.stream()
                .filter(ticket -> ticket.getSession().getId() == number)
                .filter(ticket -> !ticket.isPaid())
                .map(ticket -> ticket.getPlace().getName())
                .toList();

        if (freePlaces.isEmpty()) {
            throw new NotFoundException(FREE_PLACES_NOT_FOUND.formatted(number));
        }

        return freePlaces;
    }

    public List<ResponseSessionDto> findAll() {
        return sessionMapper.toResponseDtos(sessionRepository.findAll().orElse(emptyList()));
    }

    private SessionDto saveSession(SessionDto sessionDto) {
        return sessionMapper.toDto(sessionRepository.save(sessionMapper.toEntity(sessionDto))
                        .orElseThrow(() -> new EntitySaveException(SESSION_SAVE_ERROR_MESSAGE))
        );
    }
}
