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
    private String ADMIN_TOKEN = "secretadmin123";

    public void tokenValidation(String token) {
        if (!Objects.equals(ADMIN_TOKEN, token)) {
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);
        }
    }
}
