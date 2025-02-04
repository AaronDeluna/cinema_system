package org.javaacademy.cinema.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(PlaceProperty.class)
public class PlaceConfiguration {
    private char startRow;
    private char endRow;
    private int maxSeatsPerRow;
}
