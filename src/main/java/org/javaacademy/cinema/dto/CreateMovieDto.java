package org.javaacademy.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMovieDto {
    @NonNull
    private String name;
    @NonNull
    private String description;
}
