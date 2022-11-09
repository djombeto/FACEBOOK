package com.example.facebook.model.repositories.interfaces.post;

import com.example.facebook.model.entities.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
}
