package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.place.PlaceDto;
import org.javaacademy.cinema.dto.session.CreateSessionDto;
import org.javaacademy.cinema.dto.session.ResponseSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.entity.Movie;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.repository.MovieRepository;
import org.javaacademy.cinema.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private static final String ID_NOT_FOUND_ERROR_MESSAGE = "Ошибка: id: '%s', не найден";
    private static final String SESSION_SAVE_ERROR_MESSAGE = "Не удалось сохранить сессию";
    private static final String SESSION_NOT_FOUND_MESSAGE = "Сессии не найдены";
    private static final boolean NOT_PAID_STATUS = false;
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MovieRepository movieRepository;
    private final TicketService ticketService;

    public SessionDto create(CreateSessionDto createSessionDto) {
        Movie movie = movieRepository.findById(createSessionDto.getMovieId())
                .orElseThrow(
                        () -> new NotFoundException(
                                ID_NOT_FOUND_ERROR_MESSAGE.formatted(createSessionDto.getMovieId())
                        )
                );
        SessionDto sessionDto = sessionMapper.toDto(createSessionDto, movie);
        SessionDto responseSession = saveSession(sessionDto);
        ticketService.create(responseSession);
        return responseSession;
    }

    public List<String> findAllFreePlaceById(int number) {
        List<TicketDto> ticketDtos = ticketService.findAllByPaidStatus(NOT_PAID_STATUS);
        return ticketDtos.stream()
                .filter(ticket -> ticket.getSession().getId() == number)
                .filter(ticket -> !ticket.isPaid())
                .map(ticket -> ticket.getPlace().getName())
                .toList();
    }

    public List<ResponseSessionDto> findAll() {
        return sessionMapper.toResponseDtos(sessionRepository.findAll()
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MESSAGE))
        );
    }

    private SessionDto saveSession(SessionDto sessionDto) {
        return sessionMapper.toDto(sessionRepository.save(sessionMapper.toEntity(sessionDto))
                        .orElseThrow(() -> new EntitySaveException(SESSION_SAVE_ERROR_MESSAGE))
        );
    }
}
