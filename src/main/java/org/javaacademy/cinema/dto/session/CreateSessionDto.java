package org.javaacademy.cinema.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionDto {
    @NonNull
    @JsonProperty("movie_id")
    private Integer movieId;
    @NonNull
    private LocalDateTime datetime;
    @NonNull
    private BigDecimal price;
}
