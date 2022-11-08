package com.example.facebook.model.repositories;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import com.example.facebook.model.dtos.user.NewsFeedDTO;
import com.example.facebook.model.dtos.user.UserProfileDTO;
import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.NotFoundExceptions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class AbstractRepositories {

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
    public JdbcTemplate jdbcTemplate;

    private List<PostWithoutOwnerDTO> newsFeedQuery(long userId) {
        return jdbcTemplate.query(
                                //posts of all my friends that I follow (privacy: 'only friends')
                    "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
                    "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
                    "FROM posts AS p " +
                    "JOIN friends AS fr ON (p.owner_id = fr.friend_id) " +
                    "JOIN followers AS fl ON (p.owner_id = fl.user_id) " +
                    "JOIN users AS u ON (p.owner_id = u.id) " +
                    "WHERE fr.user_id = " + userId + " " +
                    "AND fl.follower_id = " + userId + " " +
                    "AND p.privacy = 'only friends' " +
                    "UNION " + // all public posts including these of users who are not my friends
                    "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
                    "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
                    "FROM posts AS p " +
                    "JOIN users AS u ON (p.owner_id = u.id) " +
                    "WHERE p.privacy = 'public' " +
                    "UNION " +  // all my posts where privacy = 'only friends'
                    "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
                    "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
                    "FROM posts AS p " +
                    "JOIN users AS u ON (p.owner_id = u.id) " +
                    "WHERE p.owner_id = " + userId + " " +
                    "AND p.privacy = 'only friends'" +
                    "ORDER BY created_at DESC",

                (rs, rowNum) -> new PostWithoutOwnerDTO(
                        rs.getString("full_name"),
                        rs.getLong("owner_id"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("privacy"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                )
        );
    }

    private List<CreateCommentDTO> commentsQuery(long userId) {
        return jdbcTemplate.query(
                "SELECT CONCAT(u.first_name,' ',u.last_name) AS full_name," +
                    "c.owner_id, c.id, c.content, c.created_at, c.updated_at " +
                    "FROM comments AS c " +
                    "JOIN users AS u ON (c.owner_id = u.id) " +
                    "WHERE c.post_id = " + userId + " " +
                    "ORDER BY created_at DESC",
                (rs, rowNum) -> new CreateCommentDTO(
                        rs.getString("full_name"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                )
        );
    }

    private List<PostWithoutOwnerDTO> myPostsQuery(long userId) {
        return jdbcTemplate.query(
                "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name," +
                    "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
                    "FROM posts AS p " +
                    "JOIN users AS u ON (p.owner_id = u.id) " +
                    "WHERE p.owner_id = " + userId + " " +
                    "ORDER BY created_at DESC",
                (rs, rowNum) -> new PostWithoutOwnerDTO(
                        rs.getString("full_name"),
                        rs.getLong("owner_id"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("privacy"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                )
        );
    }

    public NewsFeedDTO giveNewsfeedOnUser(User user){
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(newsFeedQuery(user.getId()));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long idPost = e.getPostId();
            e.setComments(commentsQuery(idPost));
        });
        return newsFeedDTO;
    }

    public UserProfileDTO givePostsOnUser(User user){
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        profileDTO.setMyPosts(myPostsQuery(user.getId()));
        profileDTO.getMyPosts().forEach(n -> {
            long postId = n.getPostId();
            n.setComments(commentsQuery(postId));
        });
        return profileDTO;
    }

    public User verifyUser(long uid) {
        return userRepository.findById(uid).orElseThrow(() -> {
            throw new NotFoundExceptions("User not exist");
        });
    }

    public Post verifyPost(long pid) {
        return postRepository.findById(pid).orElseThrow(() -> {
            throw new NotFoundExceptions("Post not exist");
        });
    }

    public Comment verifyComment(long cid) {
        return commentRepository.findById(cid).orElseThrow(() -> {
            throw new NotFoundExceptions("Comment not exist");
        });
    }
}
