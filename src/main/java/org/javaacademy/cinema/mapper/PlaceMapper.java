package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.place.PlaceDto;
import org.javaacademy.cinema.entity.Place;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaceMapper {

    public PlaceDto toDto(Place place) {
        return PlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .build();
    }

    public List<PlaceDto> toDtos(List<Place> places) {
        return places.stream()
                .map(this::toDto)
                .toList();
    }
}
