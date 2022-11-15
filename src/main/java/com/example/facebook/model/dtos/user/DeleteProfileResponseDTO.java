package com.example.facebook.model.dtos.user;

import java.time.LocalDateTime;

public class DeleteProfileResponseDTO extends  ChangePasswordResponseDTO{
    public DeleteProfileResponseDTO(LocalDateTime timestamp, String message) {
        super(timestamp, message);
    }
}
