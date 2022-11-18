package com.example.facebook.model.repositories.interfaces.post;

import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPostImageRepository extends JpaRepository<PostImage, Integer> {

    void deleteAllByPost(Post p);
}
