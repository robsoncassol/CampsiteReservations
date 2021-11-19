package com.upgrade.CampsiteReservations.execeptions;

import com.upgrade.CampsiteReservations.reservations.exceptions.InvalidReservationDatesException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler{


    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ExceptionDTO> handleConstraintViolationException(MethodArgumentNotValidException e) {
        log.warn("Exception was thrown processing request: {}",e.getMessage(), e);
         final var dto = ExceptionDTO.builder()
            .error("Request validation error")
            .errorDetails(String.format("One or more fields in the request are not valid: %s", e.getFieldErrors()))
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(HttpStatus.BAD_REQUEST.name())
            .build();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(dto);
    }

    @ExceptionHandler({ InvalidReservationDatesException.class })
    public ResponseEntity<ExceptionDTO> handleInconsistentReservationDatesException(InvalidReservationDatesException e) {
        log.warn("Exception was thrown processing request: {}",e.getMessage(), e);
         final var dto = ExceptionDTO.builder()
            .error("Request validation error")
            .errorDetails(e.getErrorDetails())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(HttpStatus.BAD_REQUEST.name())
            .build();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(dto);
    }
}
