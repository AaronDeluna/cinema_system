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
import org.javaacademy.cinema.dto.movie.MovieDto;
import org.javaacademy.cinema.dto.movie.ResponseMovieDto;
import org.javaacademy.cinema.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/clean_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MovieControllerTest {
    private static final int EXPECTED_LIST_SIZE = 3;
    private final Header header = new Header("user-token", "secretadmin123");
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/api/movie")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
    @Autowired
    private MovieService movieService;

    @Test
    @DisplayName("Успешное создание фильма")
    public void createSuccess() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name")
                .description("test description")
                .build();

        MovieDto movieDto = given(requestSpecification)
                .header(header)
                .body(createMovieDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(CREATED.value())
                .extract()
                .as(MovieDto.class);

        assertEquals(createMovieDto.getName(), movieDto.getName());
        assertEquals(createMovieDto.getDescription(), movieDto.getDescription());
    }

    @Test
    @DisplayName("Ошибка при попытке сохранения с некорректными данными")
    public void shouldThrowExceptionWhenSavingWithInvalidData() {
        CreateMovieDto createMovieDto = CreateMovieDto.builder()
                .name("test name, test name, test name, test name, test name, test name, test name")
                .description("test description")
                .build();

        given(requestSpecification)
                .body(createMovieDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("Успешное получение всех фильмов")
    public void getAllSuccess() {
        String name = "test name - %s";
        String description = "test name - %s";
        for (int i = 1; i <= EXPECTED_LIST_SIZE; i++) {
            CreateMovieDto createMovieDto = CreateMovieDto.builder()
                    .name(name.formatted(i))
                    .description(description.formatted(i))
                    .build();
            movieService.create(createMovieDto);
        }

        List<ResponseMovieDto> responseMovieDtos = given(requestSpecification)
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        String expectedName = name.formatted(1);
        String expectedDescription = description.formatted(1);

        assertEquals(EXPECTED_LIST_SIZE, responseMovieDtos.size());
        assertEquals(expectedName, responseMovieDtos.get(0).getName());
        assertEquals(expectedDescription, responseMovieDtos.get(0).getDescription());
    }

    @Test
    @DisplayName("Успешное получение пустого списка, если фильмы отсутствуют")
    public void shouldReturnErrorWhenMovieNotFound() {
        List<ResponseMovieDto> responseMovieDtos = given(requestSpecification)
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        int expectedSize = 0;
        assertEquals(expectedSize, responseMovieDtos.size());
    }
}
