package org.javaacademy.cinema.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketBookingDto {
    @NonNull
    @JsonProperty("session_id")
    private Integer sessionId;
    @NonNull
    @JsonProperty("place_name")
    private String placeName;
}
