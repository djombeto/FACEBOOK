package com.example.facebook.model.entities.post;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
public class PostReactionsKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    long userId;

    @Column(name = "post_id", nullable = false)
    long postId;
}
