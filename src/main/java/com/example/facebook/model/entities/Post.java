package com.example.facebook.model.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String content;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;
    @Column
    private long ownerId;
}
