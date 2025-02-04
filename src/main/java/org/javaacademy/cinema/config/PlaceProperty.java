package org.javaacademy.cinema.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cinema")
public class PlaceProperty {
    private char startRow;
    private char endRow;
    private int maxSeatsPerRow;
}
