package com.example.facebook.model.repositories.interfaces.comment;

import com.example.facebook.model.entities.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
}