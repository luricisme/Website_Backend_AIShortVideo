package com.cabybara.aishortvideo.dto.response;

public class ResponseError<T> extends ResponseData {
    public ResponseError(int status, String message) {
        super(status, message);
    }
}
