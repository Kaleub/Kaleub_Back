package com.kale.controller;

import com.kale.dto.ErrorDto;
import com.kale.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    //validation
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorDto> validException(MethodArgumentNotValidException ex) {
        log.warn(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorDto.builder()
                        .status(400)
                        .message(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                        .build()
        );
    }

    //400
    @ExceptionHandler({
            NotFoundEmailException.class,
            InvalidPasswordException.class,
            NotFoundRoomException.class,
            AlreadyInRoomException.class,
            AlreadyNotInRoomException.class,
            ExistingEmailException.class,
            OwnerCanNotLeaveException.class
    })
    public ResponseEntity<ErrorDto> InvalidRequest(final RuntimeException ex) {
        log.warn(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorDto.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .build()
        );
    }

    //401
    @ExceptionHandler({
            LoginException.class
    })
    public ResponseEntity<ErrorDto> AuthException(final RuntimeException ex) {
        log.warn(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorDto.builder()
                        .status(401)
                        .message(ex.getMessage())
                        .build()
        );
    }

    //500
    @ExceptionHandler({
            Exception.class,
            MessageFailedException.class
    })
    public ResponseEntity<ErrorDto> HandleAllException(final Exception ex) {
        log.error(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorDto.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .build()
        );
    }
}
