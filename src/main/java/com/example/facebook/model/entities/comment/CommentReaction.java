package com.example.facebook.model.entities.comment;

import com.example.facebook.model.entities.user.User;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "comment_reactions")
@Data
public class CommentReaction {

    @EmbeddedId
    CommentReactionsKey id = new CommentReactionsKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String reactionType;
}