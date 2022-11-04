package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.post.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostReactionRepository extends JpaRepository<PostReaction, Long> {
}
