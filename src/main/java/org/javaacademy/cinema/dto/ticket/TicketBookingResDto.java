package org.javaacademy.cinema.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketBookingResDto {
    @JsonProperty("ticket_id")
    private Integer ticketId;
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("movie_name")
    private String movieName;
    private LocalDateTime date;
}
