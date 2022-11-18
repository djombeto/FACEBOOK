package com.example.facebook.service;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.DeletePostResponseDTO;
import com.example.facebook.model.dtos.post.EditPostDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.entities.post.PostReaction;
import com.example.facebook.model.entities.post.PostReactionsKey;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class PostService extends AbstractService {

    public static final String CANNOT_EDIT_POST = "You cannot edit post which is not yours";
    public static final String CANNOT_DELETE_POST = "You cannot delete post which is not yours";
    public static final String CONTENT_IS_BLANK = "Content is blank";
    public static final String INVALID_REACTION = "Invalid reaction";
    public static final String DELETE_POST = "Post deleting successful";

    public NewsFeedDTO createPost(long userId, CreatePostDTO dto, long pageNumber, long rowsNumber) {
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        User user = verifyUser(userId);
        Post post = modelMapper.map(dto, Post.class);
        post.setOwner(user);
        postRepository.save(post);
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    public NewsFeedDTO editPost(long userId, long postId, EditPostDTO dto, long pageNumber, long rowsNumber) {
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        if (post.getOwner() != user) {
            throw new UnauthorizedException(CANNOT_EDIT_POST);
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BadRequestException(CONTENT_IS_BLANK);
        }
        post.setContent(dto.getContent());
        post.setPrivacy(dto.getPrivacy());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    @Transactional
    public DeletePostResponseDTO deletePost(long userId, long postId) {
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        User owner = post.getOwner();
        if (owner != user) {
            throw new UnauthorizedException(CANNOT_DELETE_POST);
        }
        commentDAO.deleteCommentsForPostID(postId);
        postDAO.deletePostForOwnerID(postId, owner.getId());
        return new DeletePostResponseDTO(LocalDateTime.now(), DELETE_POST);
    }

    public NewsFeedDTO reactToPostOrDislike(long userId, long postId, String reaction, long pageNumber,
                                                                                       long rowsNumber) {
        if (isWrongReaction(reaction)) {
            throw new BadRequestException(INVALID_REACTION);
        }
        User user = verifyUser(userId);
        Post post = verifyPost(postId);
        PostReactionsKey postReactionsKey = new PostReactionsKey();
        PostReaction postReaction = new PostReaction();

        if (isFirstReaction(userId, postId)) {
            reactToPost(userId, postId, reaction, user, post, postReactionsKey, postReaction);
        } else if (reactionTypesAreDifferent(userId, postId, reaction)) {
            postDAO.updatePostReactionType(userId, postId, reaction);
        } else {
            postDAO.deletePostReactionType(userId, postId);
        }
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    private boolean isWrongReaction(String reaction) {
        return (Arrays.stream(ReactionTypes.values())
                .noneMatch(reactionTypes -> reactionTypes.toString().equals(reaction)));
    }

    private void reactToPost(long userId, long postId, String reaction, User user, Post post,
                                PostReactionsKey postReactionsKey, PostReaction postReaction) {
        postReactionsKey.setUserId(userId);
        postReactionsKey.setPostId(postId);
        postReaction.setUser(user);
        postReaction.setPost(post);
        postReaction.setReactionType(reaction);
        postReaction.setId(postReactionsKey);
        postReactionRepository.save(postReaction);
    }

    private boolean isFirstReaction(long userId, long postId) {
        return postDAO.getPostReactionType(userId, postId).size() == 0;
    }

    private boolean reactionTypesAreDifferent(long userId, long postId, String reaction) {
        String oldReaction = postDAO.getPostReactionType(userId, postId).get(0).getReactionType();
        return !oldReaction.equals(reaction);
    }
}
