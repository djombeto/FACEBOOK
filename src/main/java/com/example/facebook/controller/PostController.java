package com.example.facebook.controller;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.DeletePostResponseDTO;
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
        long userID = getUserByID(session);
        return new ResponseEntity<>(postService.createPost(userID, dto), HttpStatus.CREATED);
    }

    @PutMapping("/posts/{pid}")
    public ResponseEntity<NewsFeedDTO> editPost(@RequestBody EditPostDTO dto,
                                                @PathVariable (name = "pid") long postID,
                                                                    HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(postService.editPost(userID, postID, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{pid}")
    public DeletePostResponseDTO deletePost(@PathVariable (name = "pid") long postID, HttpSession session){
        long userID = getUserByID(session);
        return postService.deletePost(userID, postID);
    }

    @PostMapping("/posts/{pid}/{react}")
    public ResponseEntity<NewsFeedDTO> reactToPostOrDislike(@PathVariable(name = "pid") long postID,
                                                            @PathVariable(name = "react") String reaction,
                                                            HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(postService.reactToPostOrDislike(userID, postID, reaction), HttpStatus.OK);
    }
}
