package com.example.facebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AbstractService {

    @Autowired
    protected UserService userService;
    @Autowired
    protected  CommentService commentService;
    @Autowired
    protected  PostService postService;
}
