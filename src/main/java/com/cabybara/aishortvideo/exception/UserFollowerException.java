package com.cabybara.aishortvideo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class UserFollowerException extends RuntimeException {
    private final HttpStatus status;

    public UserFollowerException(HttpStatus status, String message) {
      super(message);
      this.status = status;
    }
}
