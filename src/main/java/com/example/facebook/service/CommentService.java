package com.example.facebook.service;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.repositories.AbstractRepositories;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentService extends AbstractRepositories {

    public NewsFeedDTO commentPost(long userId, long postId, CreateCommentDTO dto) {
        User user = validateUser(userId);
        Post post = validatePost(postId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setOwner(user);
        comment.setPost(post);
        commentRepository.save(comment);
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(newsFeedQuery(userId));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long idPost = e.getPostId();
            e.setComments(commentsQuery(idPost));
        });
        return newsFeedDTO;
    }
}

