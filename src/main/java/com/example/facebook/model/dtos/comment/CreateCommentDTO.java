package com.example.facebook.model.dtos.comment;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateCommentDTO {

    public static final String TITLE = "COMMENT";

    private String title;
    private String ownerName;
    private long commentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CreateCommentDTO> comments;

    public CreateCommentDTO(){

    }

    public CreateCommentDTO(String ownerName,
                            long commentId,
                            String content,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt){
        this.title = TITLE;
        this.ownerName = ownerName;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

