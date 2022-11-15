package com.example.facebook.model.dtos.post;

import com.example.facebook.model.dtos.comment.DeleteCommentResponseDTO;

import java.time.LocalDateTime;

public class DeletePostResponseDTO extends DeleteCommentResponseDTO {
    public DeletePostResponseDTO(LocalDateTime timestamp, String message) {
        super(timestamp, message);
    }
}
