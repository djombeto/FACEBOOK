package com.example.facebook.model.dtos.post;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostWithoutOwnerDTO {

    public static final String TITLE = "POST";

    private String title;
    private String ownerName;
    private long ownerId;
    private long postId;
    private String content;
    private LocalDateTime createdAt;
    private String privacy;

    private List<CreateCommentDTO> comments;


    public PostWithoutOwnerDTO(String ownerName,
                               long ownerId,
                               long postId,
                               String content,
                               String privacy,
                               LocalDateTime createdAt){
        this.title = TITLE;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.postId = postId;
        this.content = content;
        this.privacy = privacy;
        this.createdAt = createdAt;
    }
}

