package com.example.facebook.controller;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.EditPostDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController extends AbstractController {

    @PostMapping("/posts")
    public ResponseEntity<List<PostWithoutOwnerDTO>> createPost(
                                                                    @RequestBody CreatePostDTO dto,
                                                                    HttpSession session){
        long uid = getUserId(session);
        return new ResponseEntity<>(postService.createPost(uid, dto), HttpStatus.CREATED);
    }


    @PutMapping("/posts/{pid}")
    public ResponseEntity<List<PostWithoutOwnerDTO>> editPost(
                                                                    @RequestBody EditPostDTO dto,
                                                                    @PathVariable (name = "pid")
                                                                    long postId,
                                                                    HttpSession session){
        long uid = getUserId(session);
        return new ResponseEntity<>(postService.editPost(uid, postId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{pid}")
    public void deletePost(@PathVariable (name = "pid") long postId, HttpSession session){
        long uid = getUserId(session);
        postService.deletePost(uid, postId);
    }
}
