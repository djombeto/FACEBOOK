package com.example.facebook.service;

import com.example.facebook.model.daos.CommentDAO;
import com.example.facebook.model.daos.PostDAO;
import com.example.facebook.model.daos.UserDAO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.dtos.user.UserProfileDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.NotFoundExceptions;
import com.example.facebook.model.repositories.interfaces.comment.ICommentReactionRepository;
import com.example.facebook.model.repositories.interfaces.comment.ICommentRepository;
import com.example.facebook.model.repositories.interfaces.post.IPostReactionRepository;
import com.example.facebook.model.repositories.interfaces.post.IPostRepository;
import com.example.facebook.model.repositories.interfaces.user.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    public enum ReactionTypes{
        LIKE, LOVE, CARE, HAHA, WOW, SAD, ANGRY
    }

    public NewsFeedDTO showNewsFeed(User user, long pageNumber, long rowsNumber){
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(postDAO.getNewsFeedForUserID(user.getId(), pageNumber, rowsNumber));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long postId = e.getPostId();
            e.setComments(commentDAO.getCommentsForPostID(postId));
        });
        return newsFeedDTO;
    }

    public UserProfileDTO showMyPosts(User user, long pageNumber, long rowsNumber){
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        profileDTO.setMyPosts(postDAO.getMyPostForUserID(user.getId(), pageNumber, rowsNumber));
        profileDTO.getMyPosts().forEach(n -> {
            long postId = n.getPostId();
            n.setComments(commentDAO.getCommentsForPostID(postId));
        });
        return profileDTO;
    }

    public User verifyUser(long uid) {
        return userRepository.findById(uid).orElseThrow(() -> {
            throw new NotFoundExceptions(USER_NOT_FOUND);
        });
    }

    public Post verifyPost(long pid) {
        return postRepository.findById(pid).orElseThrow(() -> {
            throw new NotFoundExceptions(POST_NOT_FOUND);
        });
    }

    public Comment verifyComment(long cid) {
        return commentRepository.findById(cid).orElseThrow(() -> {
            throw new NotFoundExceptions(COMMENT_NOT_FOUND);
        });
    }
}
