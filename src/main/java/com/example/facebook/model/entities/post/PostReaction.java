package com.example.facebook.model.entities.post;

import com.example.facebook.model.entities.user.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "post_reactions")
@Data
public class PostReaction {

    @EmbeddedId
    PostReactionsKey id = new PostReactionsKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    private String reactionType;
}
