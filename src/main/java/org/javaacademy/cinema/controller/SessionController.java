package org.javaacademy.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.session.CreateSessionDto;
import org.javaacademy.cinema.dto.session.ResponseSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDto> create(@Validated @RequestBody CreateSessionDto createSessionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.create(createSessionDto));
    }

    @GetMapping
    public ResponseEntity<List<ResponseSessionDto>> getAll() {
        return ResponseEntity.ok().body(sessionService.findAll());
    }

    @GetMapping("/{number}/free-place")
    public ResponseEntity<List<String>> getAllFreePlaceById(@PathVariable Integer number) {
        return ResponseEntity.ok().body(sessionService.findAllFreePlaceById(number));
    }
}
