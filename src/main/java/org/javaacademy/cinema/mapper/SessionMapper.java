package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public Session toEntity(SessionDto dto) {
        return new Session(
                dto.getId(),
                dto.getMovie(),
                dto.getDatetime(),
                dto.getPrice()
        );
    }

    public SessionDto toDto(Session entity) {
        return SessionDto.builder()
                .id(entity.getId())
                .movie(entity.getMovie())
                .datetime(entity.getDatetime())
                .price(entity.getPrice())
                .build();
    }
}
