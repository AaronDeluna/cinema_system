package org.javaacademy.cinema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.javaacademy.cinema.dto.ErrorResponse;
import org.javaacademy.cinema.dto.ticket.TicketBookingDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingResDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.service.TicketService;
import org.javaacademy.cinema.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {
    private static final boolean PAID_STATUS = true;
    private final TicketService ticketService;
    private final TokenService tokenService;

    @Operation(
            summary = "Получить все проданные билеты"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = TicketDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Билеты не найдены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/saled")
    public ResponseEntity<List<TicketDto>> findAllByPaidStatus(@RequestHeader("user-token") String token) {
        tokenService.tokenValidation(token);
        return ResponseEntity.ok().body(ticketService.findAllByPaidStatus(PAID_STATUS));
    }

    @Operation(
            summary = "Бронирование билета"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketBookingResDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Билет не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/booking")
    public ResponseEntity<TicketBookingResDto> booking(@RequestBody TicketBookingDto bookingDto) {
        return ResponseEntity.ok().body(ticketService.booking(bookingDto));
    }

}
