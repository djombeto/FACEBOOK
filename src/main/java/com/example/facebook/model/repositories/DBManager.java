package com.example.facebook.model.repositories;

import com.example.facebook.model.dtos.comment.CreateCommentDTO;
import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public abstract class DBManager {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public List<PostWithoutOwnerDTO> userNewsfeedQuery(long userId) {
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

    public List<CreateCommentDTO> userCommentsQuery(long userId) {
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

    public List<PostWithoutOwnerDTO> userPostsQuery(long userId) {
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
}
