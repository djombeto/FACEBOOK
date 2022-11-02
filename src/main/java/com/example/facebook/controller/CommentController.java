package com.example.facebook.controller;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;

@RestController
public class CommentController extends AbstractController {

    @PostMapping("/comments/post/{pid}")
    public ResponseEntity<NewsFeedDTO> commentPost(HttpSession session,
                                                           @RequestBody CreateCommentDTO dto,
                                                           @PathVariable long pid){
        long id = getUserId(session);
        return new ResponseEntity<>(commentService.commentPost(id, pid, dto), HttpStatus.CREATED);
    }
}
