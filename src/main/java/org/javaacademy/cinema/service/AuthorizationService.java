package org.javaacademy.cinema.service;

import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class AuthorizationService {
    public static final String INVALID_TOKEN_MESSAGE =
            "Ошибка валидации токена: предоставленный токен не совпадает с ожидаемым.";
    @Value("${cinema.admin.token}")
    private String adminToken;

    public void tokenValidation(String token) {
        if (!Objects.equals(adminToken, token)) {
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);
        }
    }
}
