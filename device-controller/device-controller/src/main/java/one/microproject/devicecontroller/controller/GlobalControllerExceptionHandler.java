package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.ExceptionResponse;
import one.microproject.devicecontroller.service.DeviceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import io.jsonwebtoken.ExpiredJwtException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DeviceException.class)
    public ResponseEntity<ExceptionResponse> handleDeviceException(DeviceException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST,
                e.getClass().getCanonicalName(), e.getMessage(), e.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.UNAUTHORIZED,
                e.getClass().getCanonicalName(), e.getMessage(), e.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
