package com.example.facebook.controller;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.EditPostDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
public class PostController extends AbstractController {

    @PostMapping("/posts")
    public ResponseEntity<NewsFeedDTO> createPost(@RequestBody CreatePostDTO dto, HttpSession session){
        long uid = getUserById(session);
        return new ResponseEntity<>(postService.createPost(uid, dto), HttpStatus.CREATED);
    }

    @PutMapping("/posts/{pid}")
    public ResponseEntity<NewsFeedDTO> editPost(@RequestBody EditPostDTO dto,
                                                @PathVariable (name = "pid") long postId,
                                                                    HttpSession session){
        long uid = getUserById(session);
        return new ResponseEntity<>(postService.editPost(uid, postId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{pid}")
    public void deletePost(@PathVariable (name = "pid") long postId, HttpSession session){
        long uid = getUserById(session);
        postService.deletePost(uid, postId);
    }

    @PostMapping("/posts/{pid}/react/{react}")
    public ResponseEntity<NewsFeedDTO> reactToPostOrDislike(@PathVariable(name = "pid") long postId,
                                                            @PathVariable(name = "react") String reaction,
                                                            HttpSession session){
        long uid = getUserById(session);
        return new ResponseEntity<>(postService.reactToPostOrDislike(uid, postId, reaction), HttpStatus.OK);
    }
}
