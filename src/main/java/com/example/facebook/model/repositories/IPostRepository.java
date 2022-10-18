package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPostRepository extends JpaRepository<Post, Long> {
}
