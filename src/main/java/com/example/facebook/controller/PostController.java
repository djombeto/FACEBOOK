package com.example.facebook.controller;

import com.example.facebook.model.dtos.post.CreatePostDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController extends AbstractController {

    @PostMapping("/posts/create")
    public ResponseEntity<List<PostWithoutOwnerDTO>> createPostProfilePage(
                                                                    @RequestBody CreatePostDTO dto,
                                                                            HttpSession session){
        long uid = getUserId(session);
        return new ResponseEntity<>(postService.createPost(uid, dto), HttpStatus.CREATED);
    }
}
