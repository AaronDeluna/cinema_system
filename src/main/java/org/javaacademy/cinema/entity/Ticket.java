package org.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Integer id;
    private Session session;
    private Place place;
    private boolean isPaid;

    public Ticket(Session session, Place place) {
        this.session = session;
        this.place = place;
    }
}
