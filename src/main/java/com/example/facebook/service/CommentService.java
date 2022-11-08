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
        return giveNewsfeedOnUser(user);
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
        if (dto.getContent().isBlank() || dto.getContent() == null){
            throw new BadRequestException("Content is blank");
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return giveNewsfeedOnUser(user);
    }

    public NewsFeedDTO reactToComment(long userId, long commentId, String reaction) {
        User user = verifyUser(userId);
        Comment comment = verifyComment(commentId);
        CommentReactionsKey commentReactionsKey = new CommentReactionsKey();
        commentReactionsKey.setUserId(userId);
        commentReactionsKey.setCommentId(commentId);
        CommentReaction commentReaction = new CommentReaction();
        commentReaction.setUser(user);
        commentReaction.setComment(comment);
        commentReaction.setReactionType(reaction);
        commentReaction.setId(commentReactionsKey);
        commentReactionRepository.save(commentReaction);
        return giveNewsfeedOnUser(user);
    }
}

