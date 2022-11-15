package com.example.facebook.controller;

import com.example.facebook.model.dtos.comment.CommentWithOwnerDTO;
import com.example.facebook.model.dtos.comment.DeleteCommentResponseDTO;
import com.example.facebook.model.dtos.comment.EditCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class CommentController extends AbstractController {

    @PostMapping("/comments/post/{pid}")
    public ResponseEntity<NewsFeedDTO> commentPost(@RequestBody CommentWithOwnerDTO dto,
                                                   @PathVariable (name = "pid") long postID,
                                                   HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(commentService.commentPost(userID, postID, dto), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{cid}")
    public ResponseEntity<NewsFeedDTO> editComment(@RequestBody EditCommentDTO dto,
                                                   @PathVariable (name = "cid") long commendID,
                                                   HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(commentService.editComment(userID, commendID, dto),OK);
    }

    @DeleteMapping("/comments/{cid}")
    public ResponseEntity<DeleteCommentResponseDTO> deleteComment(@PathVariable (name = "cid") long commentID, HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(commentService.deleteComment(userID, commentID),OK);
    }

    @PostMapping("/comments/{cid}/{react}")
    public ResponseEntity<NewsFeedDTO> reactToComment(@PathVariable(name = "cid") long commentID,
                                                      @PathVariable(name = "react") String reaction,
                                                                               HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(commentService.reactToCommentOrDislike(userID, commentID, reaction),OK);
    }
}
