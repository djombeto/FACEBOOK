package com.example.facebook.model.daos;

import com.example.facebook.model.dtos.comment.CommentReactionDTO;
import com.example.facebook.model.dtos.comment.CommentWithOwnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class CommentDAO {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public static final String SQL_GET_COMMENTS =
            "SELECT CONCAT(u.first_name,' ',u.last_name) AS full_name," +
            "c.owner_id, c.id, c.content, c.created_at, c.updated_at " +
            "FROM comments AS c " +
            "JOIN users AS u ON (c.owner_id = u.id) " +
            "WHERE c.post_id = ? " +
            "ORDER BY created_at DESC";

    public static final String SQL_DELETE_COMMENTS_FOR_POST =
            "DELETE FROM comments " +
            "WHERE post_id = ?";

    public static final String SQL_DELETE_COMMENTS_FOR_OWNER =
            "DELETE FROM comments " +
                    "WHERE id = ? " +
                    "AND owner_id = ? ";

    public static final String SQL_UPDATE_COMMENT_REACTION =
            "UPDATE comment_reactions " +
                    "SET reaction_type = ? " +
                    "WHERE user_id = ? " +
                    "AND comment_id = ? ";

    public static final String SQL_DISLIKE_COMMENT =
            "DELETE FROM comment_reactions " +
                    "WHERE user_id = ? " +
                    "AND comment_id = ? ";

    public static final String SQL_COMMENT_REACTION_TYPE =
            "SELECT * FROM comment_reactions " +
                    "WHERE user_id = ? " +
                    "AND comment_id = ?";

    public List<CommentReactionDTO> getCommentReactionType(long userId, long commentId){
        return jdbcTemplate.query(SQL_COMMENT_REACTION_TYPE,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, commentId);
                    }
                }, (rs, rowNum) -> new CommentReactionDTO(
                        rs.getString("reaction_type")
                ));
    }

    public void deleteCommentsForPostID(long postId){
        jdbcTemplate.update(SQL_DELETE_COMMENTS_FOR_POST,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, postId);
            }
        });
    }

    public void deleteCommentReaction(long userId, long commentId){
        jdbcTemplate.update(SQL_DISLIKE_COMMENT,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, commentId);
            }
        });
    }

    public void deleteCommentForOwnerId(long commentId, long ownerId){
        jdbcTemplate.update(SQL_DELETE_COMMENTS_FOR_OWNER, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, commentId);
                ps.setLong(2, ownerId);
            }
        });
    }

    public void updatePostReactionType(long userId, long commentId, String reaction){
        jdbcTemplate.update(SQL_UPDATE_COMMENT_REACTION,new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, reaction);
                ps.setLong(2, userId);
                ps.setLong(3, commentId);
            }
        });
    }

    public List<CommentWithOwnerDTO> getCommentsForPostID(long postId){
        return jdbcTemplate.query(SQL_GET_COMMENTS,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, postId);
                    }
                }, (rs, rowNum) -> new CommentWithOwnerDTO(
                        rs.getString("full_name"),
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                ));
    }
}
