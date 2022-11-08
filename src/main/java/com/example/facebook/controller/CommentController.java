package com.example.facebook.controller;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.comment.EditCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
public class CommentController extends AbstractController {

    @PostMapping("/comments/post/{pid}")
    public ResponseEntity<NewsFeedDTO> commentPost(@RequestBody CreateCommentDTO dto,
                                                   @PathVariable (name = "pid") long postId,
                                                   HttpSession session){
        long userId = getUserById(session);
        return new ResponseEntity<>(commentService.commentPost(userId, postId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{cid}")
    public ResponseEntity<NewsFeedDTO> editComment(@RequestBody EditCommentDTO dto,
                                                   @PathVariable (name = "cid") long commendId,
                                                   HttpSession session){
        long userId = getUserById(session);
        return new ResponseEntity<>(commentService.editComment(userId, commendId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{cid}")
    public void deleteComment(@PathVariable (name = "cid") long commentId, HttpSession session){
        long userId = getUserById(session);
        commentService.deleteComment(userId, commentId);
    }

    @PostMapping("/comments/{cid}/react/{react}")
    public ResponseEntity<NewsFeedDTO> reactToComment(@PathVariable(name = "cid") long commentId,
                                                      @PathVariable(name = "react") String reaction,
                                                                               HttpSession session){
        long uid = getUserById(session);
        return new ResponseEntity<>(commentService.reactToComment(uid, commentId, reaction), HttpStatus.OK);
    }
}
