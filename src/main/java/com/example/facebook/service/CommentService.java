package com.example.facebook.service;

import com.example.facebook.model.dtos.comment.CommentWithOwnerDTO;
import com.example.facebook.model.dtos.comment.DeleteCommentResponseDTO;
import com.example.facebook.model.dtos.comment.EditCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.comment.CommentReaction;
import com.example.facebook.model.entities.comment.CommentReactionsKey;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class CommentService extends AbstractService {

    public static final String DELETE_COMMENT = "Comment deleting successful";
    public static final String CANNOT_DELETE_COMMENT = "You cannot delete a comment of which you are not the owner";
    public static final String CANNOT_EDIT_COMMENT = "You cannot edit a comment of which you are not the owner";
    public static final String CONTENT_IS_BLANK = "Content is blank";
    public static final String INVALID_REACTION_TYPE = "Invalid reaction type";

    public NewsFeedDTO commentPost(long userId, long postId, CommentWithOwnerDTO dto, long pageNumber, long rowsNumber){
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setOwner(user);
        comment.setPost(post);
        commentRepository.save(comment);
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    public DeleteCommentResponseDTO deleteComment(long userId, long commentId) {
        User user = verifyUser(userId);
        Comment comment = verifyComment(commentId);
        User owner = comment.getOwner();
        if (owner != user) {
            throw new UnauthorizedException(CANNOT_DELETE_COMMENT);
        }
        commentDAO.deleteCommentForOwnerId(commentId, owner.getId());
        return new DeleteCommentResponseDTO(LocalDateTime.now(), DELETE_COMMENT);
    }

    public NewsFeedDTO editComment(long userId, long commendId, EditCommentDTO dto, long pageNumber, long rowsNumber) {
        User user = verifyUser(userId);
        Comment comment = verifyComment(commendId);
        if (comment.getOwner() != user) {
            throw new UnauthorizedException(CANNOT_EDIT_COMMENT);
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BadRequestException(CONTENT_IS_BLANK);
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    public NewsFeedDTO reactToCommentOrDislike(long userId, long commentId, String reaction, long pageNumber,
                                                                                             long rowsNumber) {
        if (isWrongReaction(reaction)) {
            throw new BadRequestException(INVALID_REACTION_TYPE);
        }
        User user = verifyUser(userId);
        Comment comment = verifyComment(commentId);
        CommentReactionsKey commentReactionsKey = new CommentReactionsKey();
        CommentReaction commentReaction = new CommentReaction();

        if (isFirstReaction(userId, commentId)) {
            reactToComment(userId, commentId, reaction, user, comment, commentReactionsKey, commentReaction);
        } else if (reactionTypesAreDifferent(userId, commentId, reaction)) {
            commentDAO.updatePostReactionType(userId, commentId, reaction);
        } else {
            commentDAO.deleteCommentReaction(userId, commentId);
        }
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    private boolean isWrongReaction(String reaction) {
        return (Arrays.stream(ReactionTypes.values())
                .noneMatch(reactionTypes -> reactionTypes.toString().equals(reaction)));
    }

    private void reactToComment(long userId, long commentId, String reaction, User user, Comment comment,
                                         CommentReactionsKey commentReactionsKey, CommentReaction commentReaction) {
        commentReactionsKey.setUserId(userId);
        commentReactionsKey.setCommentId(commentId);
        commentReaction.setUser(user);
        commentReaction.setComment(comment);
        commentReaction.setReactionType(reaction);
        commentReaction.setId(commentReactionsKey);
        commentReactionRepository.save(commentReaction);
    }

    private boolean isFirstReaction(long userId, long commentId) {
        return commentDAO.getCommentReactionType(userId, commentId).size() == 0;
    }

    private boolean reactionTypesAreDifferent(long userId, long commentId, String reaction) {
        String oldReaction = commentDAO.getCommentReactionType(userId, commentId).get(0).getReactionType();
        return !oldReaction.equals(reaction);
    }
}

