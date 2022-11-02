package com.example.facebook.model.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

    private int status;
    private LocalDateTime timestamp;
    private String message;
}
