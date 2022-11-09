package com.example.facebook.model.dtos.post;

import lombok.Data;

@Data
public class PostReactionDTO {

    private String reactionType;

    public PostReactionDTO(String reactionType){
        this.reactionType = reactionType;
    }
}
