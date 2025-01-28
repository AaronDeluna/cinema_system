package org.javaacademy.cinema.mapper;

import org.javaacademy.cinema.dto.session.ResponseSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.entity.Session;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public ResponseSessionDto toResponseDto(Session session) {
        return ResponseSessionDto.builder()
                .id(session.getId())
                .movieName(session.getMovie().getName())
                .date(session.getDatetime())
                .price(session.getPrice())
                .build();
    }

    public List<ResponseSessionDto> toResponseDtos(List<Session> sessions) {
        return sessions.stream()
                .map(this::toResponseDto)
                .toList();
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
