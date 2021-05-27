package one.microproject.devicecontroller.controller;

import one.microproject.devicecontroller.dto.ExceptionResponse;
import one.microproject.devicecontroller.service.DeviceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DeviceException.class)
    public ResponseEntity<ExceptionResponse> deviceAdminResponse(DeviceException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
