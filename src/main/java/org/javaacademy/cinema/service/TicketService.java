package org.javaacademy.cinema.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.entity.Place;
import org.javaacademy.cinema.entity.Ticket;
import org.javaacademy.cinema.mapper.SessionMapper;
import org.javaacademy.cinema.mapper.TicketMapper;
import org.javaacademy.cinema.repository.PlaceRepository;
import org.javaacademy.cinema.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final SessionMapper sessionMapper;
    private final TicketMapper ticketMapper;

    public List<TicketDto> findByPaidStatus(boolean isPaid) {
        return ticketMapper.toDtos(ticketRepository.findTicketsByPaymentStatus(isPaid)
                .orElseThrow());
    }

    public void create(SessionDto sessionDto) {
        List<Place> places = placeRepository.findAll().orElseThrow();
        for (Place place : places) {
            ticketRepository.save(new Ticket(
                    sessionMapper.toEntity(sessionDto),
                    place
            ));
        }
    }
}
