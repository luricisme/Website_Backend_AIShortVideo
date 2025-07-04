package com.cabybara.aishortvideo.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(
            {
                    MethodArgumentNotValidException.class,
                    ConstraintViolationException.class,
                    HttpMessageNotReadableException.class
            })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date(System.currentTimeMillis()));
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            errorResponse.setError("Payload invalid");
        } else if (e instanceof ConstraintViolationException) {
            message = message.substring(message.indexOf(" ") + 1);
            errorResponse.setError("Parameter invalid");
        } else if (e instanceof HttpMessageNotReadableException) {
            message = "Incorrect value enum. Correct enum values are: " + message.substring(message.indexOf("Enum class:") + 12).trim();
            errorResponse.setError("Incorrect enum value");
        }

        errorResponse.setMessage(message);
        return errorResponse;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyException(UserAlreadyExistsException e, WebRequest request) {
        log.error("=================== handleUserAlreadyException ===================");
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .error("Conflict")
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("=================== handleUserNotFoundException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .error("Not Found")
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(UserSocialAccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserSocialAccountNotFoundException(UserSocialAccountNotFoundException ex, WebRequest request) {
        log.error("=================== handleUserSocialAccountNotFoundException ===================", ex.getMessage());
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(UploadFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUploadFileException(UploadFileException ex, WebRequest request) {
        log.error("=================== handleUploadFileException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error("Bad request")
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVideoNotFoundException(VideoNotFoundException ex, WebRequest request) {
        log.error("=================== handleVideoNotFoundException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }
}
