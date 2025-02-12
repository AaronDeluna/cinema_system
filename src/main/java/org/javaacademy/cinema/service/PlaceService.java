package org.javaacademy.cinema.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.config.PlaceProperty;
import org.javaacademy.cinema.repository.PlaceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private static final String PLACE_NUMBER_FORMAT = "%s%s";
    private final PlaceRepository placeRepository;
    private final PlaceProperty placeProperty;

    @PostConstruct
    public void initPlace() {
        if (placeRepository.isPlacesAbsent()) {
            createPlace(
                    placeProperty.getStartRow(),
                    placeProperty.getEndRow(),
                    placeProperty.getMaxSeatsPerRow()
            );
        }
    }

    private void createPlace(char startRow, char endRow, int maxNumber) {
        for (char row = startRow; row <= endRow; row++) {
            for (int number = 1; number <= maxNumber; number++) {
                placeRepository.createPlace(PLACE_NUMBER_FORMAT.formatted(row, number));
            }
        }
    }
}
