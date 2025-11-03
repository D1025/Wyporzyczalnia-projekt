package com.projekt.wypozyczalnia.utils;

import com.projekt.wypozyczalnia.exceptions.InvalidHeaderException;
import com.projekt.wypozyczalnia.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SecurityValidator {

    private static final String BEARER_PREFIX = "Bearer ";

    public void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new UnauthorizedAccessException("Brak nagłówka Authorization");
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedAccessException("Nieprawidłowy typ tokena. Oczekiwano Bearer");
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedAccessException("Token autoryzacyjny jest pusty");
        }
    }

    public void validateContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            throw new InvalidHeaderException("Nagłówek Content-Type jest wymagany");
        }
    }
}
