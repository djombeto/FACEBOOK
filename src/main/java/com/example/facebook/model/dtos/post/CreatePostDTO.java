package com.example.facebook.model.dtos.post;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreatePostDTO {

    private String content;
    private String privacy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

