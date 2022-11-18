package com.example.facebook.service;

import com.example.facebook.model.daos.CommentDAO;
import com.example.facebook.model.daos.PostDAO;
import com.example.facebook.model.daos.UserDAO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.dtos.user.UserProfileDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.post.PostImage;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.NotFoundException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.repositories.interfaces.comment.ICommentReactionRepository;
import com.example.facebook.model.repositories.interfaces.comment.ICommentRepository;
import com.example.facebook.model.repositories.interfaces.post.IPostImageRepository;
import com.example.facebook.model.repositories.interfaces.post.IPostReactionRepository;
import com.example.facebook.model.repositories.interfaces.post.IPostRepository;
import com.example.facebook.model.repositories.interfaces.user.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

public abstract class AbstractService {

    @Autowired
    protected ICommentRepository commentRepository;
    @Autowired
    protected IPostRepository postRepository;
    @Autowired
    protected IUserRepository userRepository;
    @Autowired
    protected IPostReactionRepository postReactionRepository;
    @Autowired
    protected IPostImageRepository postImageRepository;
    @Autowired
    protected ICommentReactionRepository commentReactionRepository;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected BCryptPasswordEncoder encoder;
    @Autowired
    protected UserDAO userDAO;
    @Autowired
    protected PostDAO postDAO;
    @Autowired
    protected CommentDAO commentDAO;

    public static final String USER_NOT_FOUND = "User not found";
    public static final String POST_NOT_FOUND = "Post not found";
    public static final String COMMENT_NOT_FOUND = "Comment not found";
    public static final String POST_IMAGE_NOT_FOUND = "Post image not found.";

    public static final String YOU_ARE_NOT_POST_OWNER = "You are not post owner";
    protected static final String DEF_PROFILE_IMAGE_URI = "uploads" + File.separator +
                                                                            "def_profile_image.png";

    protected enum ReactionTypes{
        LIKE, LOVE, CARE, HAHA, WOW, SAD, ANGRY
    }

    protected NewsFeedDTO showNewsFeed(User user, long pageNumber, long rowsNumber){
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(postDAO.getNewsFeedForUserID(user.getId(), pageNumber, rowsNumber));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long postId = e.getPostId();
            e.setComments(commentDAO.getCommentsForPostID(postId));
        });
        return newsFeedDTO;
    }

    protected UserProfileDTO showMyPosts(User user, long pageNumber, long rowsNumber){
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        profileDTO.setMyPosts(postDAO.getMyPostForUserID(user.getId(), pageNumber, rowsNumber));
        profileDTO.getMyPosts().forEach(n -> {
            long postId = n.getPostId();
            n.setComments(commentDAO.getCommentsForPostID(postId));
        });
        return profileDTO;
    }

    protected User verifyUser(long uid) {
        return userRepository.findById(uid).orElseThrow(() -> {
            throw new NotFoundException(USER_NOT_FOUND);
        });
    }

    protected Post verifyPost(long pid) {
        return postRepository.findById(pid).orElseThrow(() -> {
            throw new NotFoundException(POST_NOT_FOUND);
        });
    }

    protected Comment verifyComment(long cid) {
        return commentRepository.findById(cid).orElseThrow(() -> {
            throw new NotFoundException(COMMENT_NOT_FOUND);
        });
    }

    protected PostImage getPostImageById(int id) {
        return postImageRepository.findById(id).orElseThrow(() -> {
            throw  new NotFoundException(POST_IMAGE_NOT_FOUND);
        });
    }

    protected void validatePostOwner(User user, Post post){
        if (post.getOwner() != user){
            throw new UnauthorizedException(YOU_ARE_NOT_POST_OWNER);
        }
    }
}
