package org.javaacademy.cinema.dto.movie;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMovieDto {
    private String name;
    private String description;
}
