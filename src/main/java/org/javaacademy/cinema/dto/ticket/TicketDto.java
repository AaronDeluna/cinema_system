package org.javaacademy.cinema.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javaacademy.cinema.entity.Place;
import org.javaacademy.cinema.entity.Session;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {
    private Integer id;
    private Session session;
    private Place place;
    private boolean isPaid;
}
