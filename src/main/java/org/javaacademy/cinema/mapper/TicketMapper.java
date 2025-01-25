package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .session(ticket.getSession())
                .place(ticket.getPlace())
                .isPaid(ticket.isPaid())
                .build();
    }

    public List<TicketDto> toDtos(List<Ticket> tickets) {
        return tickets.stream()
                .map(this::toDto)
                .toList();
    }
}

