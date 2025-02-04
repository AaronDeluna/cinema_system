package org.javaacademy.cinema.validator;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.exception.InvalidTokenException;

import java.util.Objects;

@Slf4j
@UtilityClass
public class TokenValidator {
    public static final String INVALID_TOKEN_MESSAGE =
            "Ошибка валидации токена: предоставленный токен не совпадает с ожидаемым.";

//    @Value("${cinema.admin-token}")
    private String adminToken = "secretadmin123";

    public void tokenValidation(String token) {
        log.info("adminToken: {}", adminToken);
        log.info("token: {}", token);
        if (!Objects.equals(adminToken, token)) {
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);
        }
    }
}
