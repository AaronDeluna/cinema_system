package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.place.PlaceDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingResDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.exception.TicketAlreadyPurchasedException;
import org.javaacademy.cinema.mapper.PlaceMapper;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.mapper.TicketMapper;
import org.javaacademy.cinema.repository.PlaceRepository;
import org.javaacademy.cinema.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private static final String BOOKING_TICKET_ERROR_MESSAGE = "Билет на сеанс с id: %s или местом: %s не найден.";
    private static final String NO_AVAILABLE_TICKETS_MESSAGE = "Не найдены доступные билеты для бронирования";
    private static final String TICKET_ALREADY_PURCHASED_MESSAGE = "Ошибка: место: '%s' уже занято";
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final SessionMapper sessionMapper;
    private final TicketMapper ticketMapper;
    private final PlaceMapper placeMapper;

    public void create(SessionDto sessionDto) {
        List<PlaceDto> places = placeMapper.toDtos(placeRepository.findAll().orElseThrow());
        for (PlaceDto place : places) {
            ticketRepository.save(new Ticket(
                    sessionMapper.toEntity(sessionDto),
                    placeMapper.toEntity(place)
            ));
        }
    }

    public List<TicketDto> findAllByPaidStatus(boolean isPaid) {
        return ticketMapper.toDtos(ticketRepository.findAllByPaymentStatus(isPaid)
                .orElse(Collections.emptyList()));
    }

    public TicketBookingResDto booking(TicketBookingDto bookingDto) {
        List<TicketDto> ticketsDto = ticketMapper.toDtos(
                ticketRepository.findAllBySessionId(bookingDto.getSessionId())
                        .orElseThrow(() -> new NotFoundException(NO_AVAILABLE_TICKETS_MESSAGE))
        );

        TicketDto ticketDto = ticketsDto.stream()
                .filter(ticket -> isMatchingTicket(ticket, bookingDto))
                .peek(this::validateNotPurchased)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        BOOKING_TICKET_ERROR_MESSAGE.formatted(bookingDto.getSessionId(), bookingDto.getPlaceName())
                ));

        ticketRepository.updatePurchaseStatusById(ticketDto.getId(), ticketDto.getSession().getId());
        return ticketMapper.toResponse(ticketDto);
    }

    private boolean isMatchingTicket(TicketDto ticket, TicketBookingDto bookingDto) {
        return Objects.equals(ticket.getSession().getId(), bookingDto.getSessionId())
                && Objects.equals(ticket.getPlace().getName(), bookingDto.getPlaceName());
    }

    private void validateNotPurchased(TicketDto ticket) {
        if (ticket.isPaid()) {
            throw new TicketAlreadyPurchasedException(
                    TICKET_ALREADY_PURCHASED_MESSAGE.formatted(ticket.getPlace().getName())
            );
        }
    }
}
