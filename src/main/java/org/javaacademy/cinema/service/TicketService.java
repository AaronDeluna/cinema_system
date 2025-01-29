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
import org.javaacademy.cinema.mapper.PlaceMapper;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.mapper.TicketMapper;
import org.javaacademy.cinema.repository.PlaceRepository;
import org.javaacademy.cinema.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private static final String BOOKING_TICKET_ERROR_MESSAGE = "Билет на сеанс с ID: %d или местом: %s не найден.";
    private static final String NO_AVAILABLE_TICKETS_MESSAGE = "Не найдены доступные билеты для бронирования";
    private static final boolean NOT_PAID_STATUS = false;
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final SessionMapper sessionMapper;
    private final TicketMapper ticketMapper;
    private final PlaceMapper placeMapper;

    public List<TicketDto> findByPaidStatus(boolean isPaid) {
        return ticketMapper.toDtos(ticketRepository.findTicketsByPaymentStatus(isPaid)
                .orElseThrow());
    }

    public TicketBookingResDto booking(TicketBookingDto bookingDto) {
        List<TicketDto> ticketsDto = ticketMapper.toDtos(
                ticketRepository.findTicketsByPaymentStatus(NOT_PAID_STATUS)
                        .orElseThrow(() -> new NotFoundException(NO_AVAILABLE_TICKETS_MESSAGE))
        );

        TicketDto ticketDto = ticketsDto.stream()
                .filter(t -> Objects.equals(t.getSession().getId(), bookingDto.getSessionId()))
                .filter(t -> Objects.equals(t.getPlace().getName(), bookingDto.getPlaceName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(BOOKING_TICKET_ERROR_MESSAGE));

        ticketRepository.updatePurchaseStatusById(ticketDto.getId());
        return ticketMapper.toResponse(ticketDto);
    }


    public void create(SessionDto sessionDto) {
        List<PlaceDto> places = placeMapper.toDtos(placeRepository.findAll().orElseThrow());
        for (PlaceDto place : places) {
            ticketRepository.save(new Ticket(
                    sessionMapper.toEntity(sessionDto),
                    placeMapper.toEntity(place)
            ));
        }
    }
}
