package org.javaacademy.cinema.web;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.movie.CreateMovieDto;
import org.javaacademy.cinema.dto.session.CreateSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingDto;
import org.javaacademy.cinema.dto.ticket.TicketBookingResDto;
import org.javaacademy.cinema.dto.ticket.TicketDto;
import org.javaacademy.cinema.service.MovieService;
import org.javaacademy.cinema.service.SessionService;
import org.javaacademy.cinema.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TicketControllerTest {
    private static final BigDecimal MOVIE_PRICE = valueOf(1000);
    private final Header header = new Header("user-token",  "secretadmin123");
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/api/ticket")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build()
            .header(header);
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private SessionService sessionService;

    @Test
    @DisplayName("Успешное получение купленых билетов")
    public void findAllByPaidStatusSuccess() {
        String expectedName = "test name";
        String expectedDescription = "test description";
        String expectedPlace = "A3";
        LocalDateTime dateTime = LocalDateTime.now();

        TicketBookingDto ticketBookingDto = createTicket(
                expectedName,
                expectedDescription,
                expectedPlace,
                dateTime
        );

        ticketService.booking(ticketBookingDto);

        List<TicketDto> ticketDtos = given(requestSpecification)
                .get("/saled")
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        assertEquals(1, ticketDtos.size());
    }

    @Test
    @DisplayName("Успешное бронирование билета")
    public void bookingSuccess() {
        String expectedName = "test name";
        String expectedDescription = "test description";
        String expectedPlace = "A3";
        LocalDateTime dateTime = LocalDateTime.now();

        TicketBookingDto ticketBookingDto = createTicket(
                expectedName,
                expectedDescription,
                expectedPlace,
                dateTime
        );

        TicketBookingResDto ticketBookingResDto = given(requestSpecification)
                .body(ticketBookingDto)
                .post("/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(TicketBookingResDto.class);

        assertEquals(ticketBookingDto.getPlaceName(), ticketBookingResDto.getPlaceName());
        assertEquals(expectedName, ticketBookingResDto.getMovieName());
        assertEquals(dateTime, ticketBookingResDto.getDate());
    }

    @Test
    @DisplayName("Ошибка при попытке забронировать купленный билет")
    public void shouldFailWhenBookingPurchasedTicket() {
        String expectedName = "test name";
        String expectedDescription = "test description";
        String expectedPlace = "A3";
        LocalDateTime dateTime = LocalDateTime.now();

        TicketBookingDto ticketBookingDto = createTicket(
                expectedName,
                expectedDescription,
                expectedPlace,
                dateTime
        );

        ticketService.booking(ticketBookingDto);
        ticketService.booking(ticketBookingDto);

        given(requestSpecification)
                .body(ticketBookingDto)
                .post("/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(CONFLICT.value());
    }

    @Test
    @DisplayName("Ошибка при попытке забронировать билет на несуществующий сеанс")
    public void shouldThrowExceptionWhenBookingNonexistentSession() {
        TicketBookingDto ticketBookingDto = TicketBookingDto.builder()
                .sessionId(1)
                .placeName("A1")
                .build();

        given(requestSpecification)
                .body(ticketBookingDto)
                .post("/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(NOT_FOUND.value());
    }

    private TicketBookingDto createTicket(String name, String desc, String place, LocalDateTime date) {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name(name)
                .description(desc)
                .build();
        int movieId = movieService.create(createMovieDto).getId();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(date)
                .price(MOVIE_PRICE)
                .build();

        SessionDto sessionDto = sessionService.create(createSessionDto);

        ticketService.create(sessionDto);

        return TicketBookingDto.builder()
                .sessionId(sessionDto.getId())
                .placeName(place)
                .build();
    }
}
