package com.example.facebook.service;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.EditPostDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import com.example.facebook.model.entities.Post;
import com.example.facebook.model.entities.User;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.repositories.AbstractRepositories;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService extends AbstractRepositories {
    public List<PostWithoutOwnerDTO> createPost(long userId, CreatePostDTO dto) {
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        User user = validateUser(userId);
        Post post = modelMapper.map(dto, Post.class);
        post.setOwner(user);
        postRepository.save(post);
        List<PostWithoutOwnerDTO> postWithoutOwnerDTOS = myPostsQuery(userId);
        postWithoutOwnerDTOS.forEach(p -> {
            long postId = p.getPostId();
            p.setComments(commentsQuery(postId));
        });
        return postWithoutOwnerDTOS;
    }

    public List<PostWithoutOwnerDTO> editPost(long userId, long postId, EditPostDTO dto) {
        dto.setUpdatedAt(LocalDateTime.now());
        User user = validateUser(userId);
        Post post = validatePost(postId);
        if (post.getOwner() != user){
            throw new UnauthorizedException("You cannot edit post which is not yours");
        }
        post.setContent(dto.getContent());
        post.setPrivacy(dto.getPrivacy());
        post.setUpdatedAt(dto.getUpdatedAt());
        postRepository.save(post);
        List<PostWithoutOwnerDTO> postWithoutOwnerDTOS = myPostsQuery(userId);
        postWithoutOwnerDTOS.forEach(p -> {
            long pid = p.getPostId();
            p.setComments(commentsQuery(pid));
        });
        return postWithoutOwnerDTOS;
    }

    public void deletePost(long userId, long postId) {
        User user = validateUser(userId);
        Post post = validatePost(postId);
        if (post.getOwner() != user){
            throw new UnauthorizedException("You cannot delete post which is not yours");
        }
        jdbcTemplate.execute("DELETE FROM comments " +
                                 "WHERE post_id = " + postId);

        jdbcTemplate.execute("DELETE FROM posts " +
                                 "WHERE id = " + postId +  " " +
                                 "AND owner_id = " + userId);
    }
}
