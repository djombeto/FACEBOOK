package com.example.facebook.model.dtos.comment;

import com.example.facebook.model.dtos.user.ChangePasswordResponseDTO;

import java.time.LocalDateTime;

public class DeleteCommentResponseDTO extends ChangePasswordResponseDTO {
    public DeleteCommentResponseDTO(LocalDateTime timestamp, String message) {
        super(timestamp, message);
    }
}
