package org.javaacademy.cinema.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseSessionDto {
    private Integer id;
    @JsonProperty("movie_name")
    private String movieName;
    private LocalDateTime date;
    private BigDecimal price;
}
