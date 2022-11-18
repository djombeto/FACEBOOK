package com.example.facebook.model.entities.post;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "post_pictures")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;
    @Column
    private String imageUri;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
