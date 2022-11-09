package com.example.facebook.service;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.comment.EditCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.comment.CommentReaction;
import com.example.facebook.model.entities.comment.CommentReactionsKey;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.repositories.AbstractRepositories;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class CommentService extends AbstractRepositories {

    public NewsFeedDTO commentPost(long userId, long postId, CreateCommentDTO dto) {
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setOwner(user);
        comment.setPost(post);
        commentRepository.save(comment);
        return giveNewsfeedForUser(user);
    }

    public void deleteComment(long userId, long commentId) {
        User user = verifyUser(userId);
        Comment comment = verifyComment(commentId);
        if (comment.getOwner() != user) {
            throw new UnauthorizedException("You cannot delete a comment of which you are not the owner");
        }
        commentRepository.deleteCommentById(commentId);
    }

    public NewsFeedDTO editComment(long userId, long commendId, EditCommentDTO dto) {
        User user = verifyUser(userId);
        Comment comment = verifyComment(commendId);
        if (comment.getOwner() != user) {
            throw new UnauthorizedException("You cannot edit a comment of which you are not the owner");
        }
        if (dto.getContent().isBlank() || dto.getContent() == null) {
            throw new BadRequestException("Content is blank");
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return giveNewsfeedForUser(user);
    }

    public NewsFeedDTO reactToComment(long userId, long commentId, String reaction) {
        if (Arrays.stream(ReactionTypes.values())
                .noneMatch(reactionTypes -> reactionTypes.toString().equals(reaction))) {
            throw new BadRequestException("Invalid reaction");
        }
        User user = verifyUser(userId);
        Comment comment = verifyComment(commentId);
        CommentReactionsKey commentReactionsKey = new CommentReactionsKey();
        CommentReaction commentReaction = new CommentReaction();
        if (isFirstReact(userId, commentId, "comment_reactions", "comment_id")) {
            commentReactionsKey.setUserId(userId);
            commentReactionsKey.setCommentId(commentId);
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            commentReaction.setReactionType(reaction);
            commentReaction.setId(commentReactionsKey);
            commentReactionRepository.save(commentReaction);
        } else if (!isFirstReact(userId, commentId, "comment_reactions", "comment_id") &&
                !giveReactionType(userId, commentId, "comment_reactions", "comment_id")
                        .get(0)
                        .getReactionType()
                        .equals(reaction)) {

            jdbcTemplate.update("UPDATE comment_reactions " +
                    "SET reaction_type = '" + reaction + "' " +
                    "WHERE user_id = " + userId + " " +
                    "AND comment_id = " + commentId);
        } else {
            jdbcTemplate.execute("DELETE FROM comment_reactions " +
                    "WHERE user_id = " + userId + " " +
                    "AND comment_id = " + commentId);
        }
        return giveNewsfeedForUser(user);
    }
}

