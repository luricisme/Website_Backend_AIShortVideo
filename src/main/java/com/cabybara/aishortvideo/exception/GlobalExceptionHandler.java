package com.cabybara.aishortvideo.exception;

import com.google.api.client.auth.oauth2.TokenResponseException;
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

    @ExceptionHandler(UserFollowerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserFollowerException(UserFollowerException ex, WebRequest request) {
        log.error("=================== handleUserNotFoundException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .error("Bad request")
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

    @ExceptionHandler(TiktokApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTiktokApiException(TiktokApiException ex, WebRequest request) {
        log.error("=================== handleTiktokApiException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(DashboardException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDashboardException(DashboardException ex, WebRequest request) {
        log.error("=================== handleDashboardException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(TokenResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleYoutubeTokenResponseException(DashboardException ex, WebRequest request) {
        log.error("=================== handleYoutubeTokenResponseException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Token has expired, please login again")
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(YoutubeApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleYoutubeApiException(YoutubeApiException ex, WebRequest request) {
        log.error("=================== handleYoutubeApiException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }

    @ExceptionHandler(InvalidParamsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidParamsException(InvalidParamsException ex, WebRequest request) {
        log.error("=================== handleInvalidParamsException ===================", ex);
        return ErrorResponse.builder()
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.name())
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }
}
