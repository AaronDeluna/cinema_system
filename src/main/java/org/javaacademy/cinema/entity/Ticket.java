package org.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Integer id;
    private Session session;
    private Place place;
    private PaymentStatus paymentStatus;
}
