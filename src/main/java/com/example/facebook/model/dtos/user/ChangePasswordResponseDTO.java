package com.example.facebook.model.dtos.user;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ChangePasswordResponseDTO {

    private LocalDateTime timestamp;
    private String message;

    public ChangePasswordResponseDTO(LocalDateTime timestamp, String message){
        this.timestamp = timestamp;
        this.message = message;
    }
}
