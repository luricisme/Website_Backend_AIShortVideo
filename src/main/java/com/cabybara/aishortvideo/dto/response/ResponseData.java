package com.cabybara.aishortvideo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseData<T> {
    private final int status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseData(HttpStatus status, String message, T data){
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public ResponseData(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseData(int status, String message){
        this.status = status;
        this.message = message;
    }
}