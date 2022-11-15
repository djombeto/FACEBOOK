package com.example.facebook.model.dtos.comment;

import com.example.facebook.model.dtos.post.PostReactionDTO;

public class CommentReactionDTO extends PostReactionDTO {
    public CommentReactionDTO(String reactionType) {
        super(reactionType);
    }
}
