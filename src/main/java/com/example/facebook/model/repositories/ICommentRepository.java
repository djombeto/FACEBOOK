package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
}
