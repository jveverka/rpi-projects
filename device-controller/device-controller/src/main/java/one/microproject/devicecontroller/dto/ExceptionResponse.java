package one.microproject.devicecontroller.dto;

import org.springframework.http.HttpStatus;

public record ExceptionResponse(HttpStatus status, String message) {
}
