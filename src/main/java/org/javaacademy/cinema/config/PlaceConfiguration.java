package org.javaacademy.cinema.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("cinema.places")
public class PlaceConfiguration {
    private char startRow;
    private char endRow;
    private int maxSeatsPerRow;
}
