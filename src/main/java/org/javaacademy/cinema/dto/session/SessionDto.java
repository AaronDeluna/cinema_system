package org.javaacademy.cinema.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javaacademy.cinema.entity.Movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private Integer id;
    private Movie movie;
    private LocalDateTime datetime;
    private BigDecimal price;
}
