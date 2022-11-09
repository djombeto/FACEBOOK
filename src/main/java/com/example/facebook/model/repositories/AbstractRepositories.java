package com.example.facebook.model.repositories;

import com.example.facebook.model.dtos.post.PostReactionDTO;
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

import java.util.List;

public abstract class AbstractRepositories extends DBManager {

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


    public enum ReactionTypes{
        LIKE, LOVE, CARE, HAHA, WOW, SAD, ANGRY
    }

    public NewsFeedDTO giveNewsfeedForUser(User user){
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(userNewsfeedQuery(user.getId()));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long idPost = e.getPostId();
            e.setComments(userCommentsQuery(idPost));
        });
        return newsFeedDTO;
    }

    public UserProfileDTO givePostsForUser(User user){
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        profileDTO.setMyPosts(userPostsQuery(user.getId()));
        profileDTO.getMyPosts().forEach(n -> {
            long postId = n.getPostId();
            n.setComments(userCommentsQuery(postId));
        });
        return profileDTO;
    }

    public User verifyUser(long uid) {
        return userRepository.findById(uid).orElseThrow(() -> {
            throw new NotFoundExceptions("User not found");
        });
    }

    public Post verifyPost(long pid) {
        return postRepository.findById(pid).orElseThrow(() -> {
            throw new NotFoundExceptions("Post not found");
        });
    }

    public Comment verifyComment(long cid) {
        return commentRepository.findById(cid).orElseThrow(() -> {
            throw new NotFoundExceptions("Comment not found");
        });
    }

    public List<PostReactionDTO> giveReactionType(long userId, long postOrCommentId,
                                                  String tableInDB, String postOrComment_id) {
        return jdbcTemplate.query(
                "SELECT * FROM " + tableInDB + " " +
                        "WHERE user_id = " + userId + " " +
                        "AND " + postOrComment_id  + " = " + postOrCommentId,
                (rs, rowNum) -> new PostReactionDTO(
                        rs.getString("reaction_type")
                )
        );
    }

    public boolean isFirstReact(long userId, long postOrCommentId, String tableInDB, String postOrComment_id) {
        return jdbcTemplate.query(
                "SELECT * FROM " + tableInDB + " " +
                        "WHERE user_id = " + userId + " " +
                        "AND " + postOrComment_id  + " = " + postOrCommentId,
                (rs, rowNum) -> new PostReactionDTO(
                        rs.getString("reaction_type")
                )
        ).isEmpty();
    }
}
