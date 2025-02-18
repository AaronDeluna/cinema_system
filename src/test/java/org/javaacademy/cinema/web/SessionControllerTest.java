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
import org.javaacademy.cinema.dto.session.ResponseSessionDto;
import org.javaacademy.cinema.dto.session.SessionDto;
import org.javaacademy.cinema.service.MovieService;
import org.javaacademy.cinema.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SessionControllerTest {
    private static final BigDecimal MOVIE_PRICE = valueOf(1000);
    private static final BigDecimal NOT_CORRECT_MOVIE_PRICE = valueOf(10000000000L);
    private static final int SESSION_COUNT = 3;
    private final Header header = new Header("user-token", "secretadmin123");
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/api/session")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build()
            .header(header);
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
    @Autowired
    private MovieService movieService;
    @Autowired
    private SessionService sessionService;

    @Test
    @DisplayName("Успешное создание сеанса")
    public void createSuccess() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();

        int movieId = movieService.create(createMovieDto).getId();
        LocalDateTime dateTime = LocalDateTime.now();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(dateTime)
                .price(MOVIE_PRICE)
                .build();

        SessionDto sessionDto = given(requestSpecification)
                .body(createSessionDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(CREATED.value())
                .extract()
                .as(SessionDto.class);

        assertEquals(createMovieDto.getName(), sessionDto.getMovie().getName());
        assertEquals(createMovieDto.getDescription(), sessionDto.getMovie().getDescription());
        assertEquals(createSessionDto.getMovieId(), sessionDto.getMovie().getId());
        assertEquals(createSessionDto.getDatetime(), sessionDto.getDatetime());
        assertEquals(createSessionDto.getPrice(), sessionDto.getPrice());
    }

    @Test
    @DisplayName("Ошибка при попытке создания сеанса для несуществующего фильма")
    public void shouldFailToCreateSessionForNonExistentMovie() {
        LocalDateTime dateTime = LocalDateTime.now();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(1)
                .datetime(dateTime)
                .price(MOVIE_PRICE)
                .build();

        given(requestSpecification)
                .body(createSessionDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Ошибка при попытке создания сеанса c некорректными данными")
    public void shouldFailToCreateSessionWithInvalidData() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();

        int movieId = movieService.create(createMovieDto).getId();
        LocalDateTime dateTime = LocalDateTime.now();

        CreateSessionDto createSessionDto = CreateSessionDto.builder()
                .movieId(movieId)
                .datetime(dateTime)
                .price(NOT_CORRECT_MOVIE_PRICE)
                .build();

        given(requestSpecification)
                .body(createSessionDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("Успешное получение всех сеансов")
    public void getAllSuccess() {
        String movieName = "test name: %s";
        String movieDescription = "test description: %s";
        generateMoviesWithSessions(movieName, movieDescription, MOVIE_PRICE);

        List<ResponseSessionDto> sessionDtos = given(requestSpecification)
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        String expectedMovieName = "test name: %s".formatted(1);

        assertEquals(SESSION_COUNT, sessionDtos.size());
        assertEquals(expectedMovieName, sessionDtos.get(0).getMovieName());
        assertEquals(0, MOVIE_PRICE.compareTo(sessionDtos.get(0).getPrice()));
    }

    @Test
    @DisplayName("Ошибка при попытке получить доступ к свободным местам для несуществующего id сеанса")
    public void shouldReturnErrorWhenGettingFreeSeatsForNonExistentSession() {
        given(requestSpecification)
                .get("/10/free-place")
                .then()
                .spec(responseSpecification)
                .statusCode(NOT_FOUND.value());
    }

    private void generateMoviesWithSessions(String movieName, String movieDescription, BigDecimal price) {
        for (int i = 1; i <= SESSION_COUNT; i++) {
            CreateMovieDto createMovieDto = CreateMovieDto.builder()
                    .name(movieName.formatted(i))
                    .description(movieDescription.formatted(i))
                    .build();

            int movieId = movieService.create(createMovieDto).getId();
            LocalDateTime dateTime = LocalDateTime.now();

            sessionService.create(CreateSessionDto.builder()
                    .movieId(movieId)
                    .datetime(dateTime)
                    .price(price)
                    .build());
        }
    }
}
