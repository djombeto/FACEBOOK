package com.example.facebook.service;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.EditPostDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.entities.post.PostReaction;
import com.example.facebook.model.entities.post.PostReactionsKey;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.repositories.AbstractRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class PostService extends AbstractRepositories {

    public NewsFeedDTO createPost(long userId, CreatePostDTO dto) {
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        User user = verifyUser(userId);
        Post post = modelMapper.map(dto, Post.class);
        post.setOwner(user);
        postRepository.save(post);
        return giveNewsfeedForUser(user);
    }

    public NewsFeedDTO editPost(long userId, long postId, EditPostDTO dto) {
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        if (post.getOwner() != user) {
            throw new UnauthorizedException("You cannot edit post which is not yours");
        }
        if (dto.getContent().isBlank() || dto.getContent() == null) {
            throw new BadRequestException("Content is blank");
        }
        post.setContent(dto.getContent());
        post.setPrivacy(dto.getPrivacy());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return giveNewsfeedForUser(user);
    }

    @Transactional
    public void deletePost(long userId, long postId) {
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        if (post.getOwner() != user) {
            throw new UnauthorizedException("You cannot delete post which is not yours");
        }
        jdbcTemplate.execute("DELETE FROM comments " +
                "WHERE post_id = " + postId);

        jdbcTemplate.execute("DELETE FROM posts " +
                "WHERE id = " + postId + " " +
                "AND owner_id = " + userId);
    }

    public NewsFeedDTO reactToPostOrDislike(long userId, long postId, String reaction) {
        if (Arrays.stream(ReactionTypes.values())
                .noneMatch(reactionTypes -> reactionTypes.toString().equals(reaction))) {
            throw new BadRequestException("Invalid reaction");
        }
       User user = verifyUser(userId);
       Post post = verifyPost(postId);
       PostReactionsKey postReactionsKey = new PostReactionsKey();
       PostReaction postReaction = new PostReaction();
       if (isFirstReact(userId, postId, "post_reactions", "post_id")) {
           postReactionsKey.setUserId(userId);
           postReactionsKey.setPostId(postId);
           postReaction.setUser(user);
           postReaction.setPost(post);
           postReaction.setReactionType(reaction);
           postReaction.setId(postReactionsKey);
           postReactionRepository.save(postReaction);
       }
       else if (!isFirstReact(userId,postId,"post_reactions", "post_id") &&
               !giveReactionType(userId,postId,"post_reactions", "post_id")
                                                   .get(0)
                                                   .getReactionType()
                                                   .equals(reaction)) {

           jdbcTemplate.update("UPDATE post_reactions " +
                                       "SET reaction_type = '" + reaction + "' " +
                                       "WHERE user_id = " + userId + " " +
                                       "AND post_id = " + postId);
       } else {
           jdbcTemplate.execute("DELETE FROM post_reactions " +
                                     "WHERE user_id = " + userId + " " +
                                     "AND post_id = " + postId);
       }
        return giveNewsfeedForUser(user);
    }
}
