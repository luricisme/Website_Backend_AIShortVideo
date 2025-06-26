package com.cabybara.aishortvideo.dto.response;

import org.springframework.http.HttpStatus;

public class ResponseError<T> extends ResponseData {
    public ResponseError(HttpStatus status, String message) {
        super(status.value(), message);
    }

    public ResponseError(int status, String message) {
        super(status, message);
    }
}
