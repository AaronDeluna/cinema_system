package org.javaacademy.cinema.web;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TicketControllerTest {
    private static final String DELETE_TABLES_SQL = """
            DELETE FROM ticket;
            DELETE FROM place;
            DELETE FROM session;
            DELETE FROM movie;
            """;
    private final Header header = new Header("user-token", "secretadmin123");
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
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private SessionService sessionService;

    @AfterEach
    public void cleanUpDatabase() {
        jdbcTemplate.execute(DELETE_TABLES_SQL);
    }

    @Test
    @DisplayName("Успешное получение купленых билетов")
    public void findAllByPaidStatusSuccess() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();
        int movieId = movieService.create(createMovieDto).getId();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(LocalDateTime.now())
                .price(BigDecimal.valueOf(100))
                .build();

        SessionDto sessionDto = sessionService.create(createSessionDto);

        ticketService.create(sessionDto);

        TicketBookingDto ticketBookingDto = TicketBookingDto.builder()
                .sessionId(sessionDto.getId())
                .placeName("A3")
                .build();

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
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();
        int movieId = movieService.create(createMovieDto).getId();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(LocalDateTime.now())
                .price(BigDecimal.valueOf(100))
                .build();

        SessionDto sessionDto = sessionService.create(createSessionDto);

        ticketService.create(sessionDto);

        TicketBookingDto ticketBookingDto = TicketBookingDto.builder()
                .sessionId(sessionDto.getId())
                .placeName("A3")
                .build();

        TicketBookingResDto ticketBookingResDto = given(requestSpecification)
                .body(ticketBookingDto)
                .post("/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(TicketBookingResDto.class);

        assertEquals(ticketBookingDto.getPlaceName(), ticketBookingResDto.getPlaceName());
        assertEquals(sessionDto.getMovie().getName(), ticketBookingResDto.getMovieName());
        assertEquals(sessionDto.getDatetime(), ticketBookingResDto.getDate());
    }

    @Test
    @DisplayName("Ошибка при попытке забронировать купленный билет")
    public void shouldFailWhenBookingPurchasedTicket() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();
        int movieId = movieService.create(createMovieDto).getId();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(LocalDateTime.now())
                .price(BigDecimal.valueOf(100))
                .build();

        SessionDto sessionDto = sessionService.create(createSessionDto);

        ticketService.create(sessionDto);

        TicketBookingDto ticketBookingDto = TicketBookingDto.builder()
                .sessionId(sessionDto.getId())
                .placeName("A1")
                .build();

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



}
