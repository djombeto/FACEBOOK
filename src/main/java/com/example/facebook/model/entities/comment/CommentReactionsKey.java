package com.example.facebook.model.entities.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Embeddable
@EqualsAndHashCode
public class CommentReactionsKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    long userId;

    @Column(name = "comment_id", nullable = false)
    long commentId;
}

