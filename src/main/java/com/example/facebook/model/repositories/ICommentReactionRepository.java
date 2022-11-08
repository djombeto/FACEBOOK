package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.comment.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentReactionRepository extends JpaRepository<CommentReaction, Long> {
}
