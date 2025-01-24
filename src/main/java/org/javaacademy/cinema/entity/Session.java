package org.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Integer id;
    private Movie movie;
    private LocalDateTime datetime;
    private BigDecimal price;

    public Session(Movie movie, LocalDateTime datetime, BigDecimal price) {
        this.movie = movie;
        this.datetime = datetime;
        this.price = price;
    }
}
