package org.javaacademy.cinema.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties(PlaceProperty.class)
public class PlaceConfiguration {
}
