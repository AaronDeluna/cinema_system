package org.javaacademy.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.ticket.TicketBookingDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingResponse;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/saled")
    public ResponseEntity<List<TicketDto>> findByPaidStatus() {
        return ResponseEntity.ok().body(ticketService.findByPaidStatus(true));
    }

    @PostMapping("/booking")
    public ResponseEntity<TicketBookingResponse> booking(@RequestBody TicketBookingDto bookingDto) {
        return ResponseEntity.ok().body(ticketService.booking(bookingDto));
    }

}
