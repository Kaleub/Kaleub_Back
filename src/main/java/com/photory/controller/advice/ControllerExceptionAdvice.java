package com.photory.controller.advice;

import com.photory.common.dto.ApiResponse;
import com.photory.common.exception.model.*;
import com.photory.common.dto.ErrorDto;
import com.photory.common.exception.test.PhotoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ValidationException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {

    /**
     * Photory Custom Exception
     */
    @ExceptionHandler(PhotoryException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBaseException(PhotoryException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.error(exception.getErrorCode()));
    }

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
            ValidationException.class,
            AlreadyInRoomException.class,
            AlreadyNotInRoomException.class,
            UserAlreadyNotInRoomException.class,
            OwnerCanNotLeaveException.class,
            AlertLeaveRoomException.class,
            NotAloneException.class,
            NotInRoomException.class,
            ExceedRoomCapacityException.class
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
