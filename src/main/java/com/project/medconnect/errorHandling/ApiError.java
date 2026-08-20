package com.project.medconnect.errorHandling;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Setter
public class ApiError {
    private LocalDateTime timestamp;
    private String message;
    private String error;
    private int status;
    private Map<String , String> fields;
    public ApiError(int status, String message, String error) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.error = error;
    }

}
