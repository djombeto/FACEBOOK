package com.example.facebook.service;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import com.example.facebook.model.entities.Post;
import com.example.facebook.model.entities.User;
import com.example.facebook.model.repositories.AbstractRepositories;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService extends AbstractRepositories {
    public List<PostWithoutOwnerDTO> createPost(long userId, CreatePostDTO dto) {
        User user = validateUser(userId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
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
}
