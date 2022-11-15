package com.example.facebook.model.daos;

import com.example.facebook.model.dtos.post.PostReactionDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class PostDAO {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public static final String SQL_NEWS_FEED =
            "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
            "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
            "FROM posts AS p " +
            "JOIN friends AS fr ON (p.owner_id = fr.friend_id) " +
            "JOIN followers AS fl ON (p.owner_id = fl.user_id) " +
            "JOIN users AS u ON (p.owner_id = u.id) " +
            "WHERE fr.user_id = ? " +
            "AND fl.follower_id = ? " +
            "AND p.privacy = 'only friends' " +
            "UNION " +
            "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
            "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
            "FROM posts AS p " +
            "JOIN users AS u ON (p.owner_id = u.id) " +
            "WHERE p.privacy = 'public' " +
            "UNION " +
            "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name, " +
            "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
            "FROM posts AS p " +
            "JOIN users AS u ON (p.owner_id = u.id) " +
            "WHERE p.owner_id = ? " +
            "AND p.privacy = 'only friends'"  +
            "ORDER BY created_at DESC";

    public static final String SQL_MY_POSTS =
            "SELECT CONCAT (u.first_name,' ',u.last_name) AS full_name," +
            "p.owner_id, p.id, p.content, p.privacy, p.created_at, p.updated_at " +
            "FROM posts AS p " +
            "JOIN users AS u ON (p.owner_id = u.id) " +
            "WHERE p.owner_id = ? " +
            "ORDER BY created_at DESC";

    public static final String SQL_UPDATE_POST_REACTION =
            "UPDATE post_reactions " +
                    "SET reaction_type = ? " +
                    "WHERE user_id = ? " +
                    "AND post_id = ?";

    public static final String SQL_GET_POST_REACTION_TYPE =
            "SELECT * FROM post_reactions " +
                    "WHERE user_id = ? " +
                    "AND post_id = ?";


    public static final String SQL_DISLIKE_POSTS =
            "DELETE FROM post_reactions " +
                    "WHERE user_id = ? " +
                    "AND post_id = ?";


    public static final String SQL_DELETE_POST =
            "DELETE FROM posts " +
                    "WHERE id = ?" +
                    "AND owner_id = ?";


    public List<PostWithoutOwnerDTO> getNewsFeedForUserID(long userId){
        return jdbcTemplate.query(SQL_NEWS_FEED,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, userId);
                        ps.setLong(3, userId);
                    }
                }, (rs, rowNum) -> new PostWithoutOwnerDTO(
                        rs.getString("full_name"),
                        rs.getLong("owner_id"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("privacy"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                ));
    }

    public List<PostWithoutOwnerDTO> getMyPostForUserID(long userId){
        return jdbcTemplate.query(SQL_MY_POSTS,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                    }
                }, (rs, rowNum) -> new PostWithoutOwnerDTO(
                        rs.getString("full_name"),
                        rs.getLong("owner_id"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("privacy"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                ));
    }

    public void updatePostReactionType(long userId, long postId, String reaction){
        jdbcTemplate.update(SQL_UPDATE_POST_REACTION,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, reaction);
                ps.setLong(2, userId);
                ps.setLong(3, postId);
            }
        });
    }

    public List<PostReactionDTO> getPostReactionType(long userId, long postId){
        return jdbcTemplate.query(SQL_GET_POST_REACTION_TYPE,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, postId);
                    }
                }, (rs, rowNum) -> new PostReactionDTO(
                        rs.getString("reaction_type")
                ));
    }

    public void deletePostForOwnerID(long postId, long ownerId){
        jdbcTemplate.update(SQL_DELETE_POST,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, postId);
                ps.setLong(2, ownerId);
            }
        });
    }

    public void deletePostReactionType(long userId, long postId){
        jdbcTemplate.update(SQL_DISLIKE_POSTS,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, postId);
            }
        });
    }
}
