package com.example.facebook.model.daos;

import com.example.facebook.model.dtos.user.UserWithoutPassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDAO {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public static final String SQL_FIND_FRIEND_BY_ID =
            "SELECT u.* FROM users AS u " +
                    "JOIN friends as fr ON (u.id = fr.friend_id) " +
                    "WHERE fr.user_id = ? " +
                    "AND fr.friend_id = ?";

    public static final String SQL_FIND_ALL_FRIENDS =
            "SELECT u.* FROM users AS u " +
                    "JOIN friends as fr ON (u.id = fr.friend_id) " +
                    "WHERE fr.user_id = ? " +
                    "LIMIT ?, ?";

    public static final String SQL_FIND_FOLLOWER_BY_ID =
            "SELECT u.* FROM users AS u "+
                    "JOIN followers as fl ON (u.id = fl.follower_id) " +
                    "WHERE fl.user_id = ? " +
                    "AND fl.follower_id = ?";

    public static final String SQL_FIND_FRIENDS_OF_MY_FRIENDS =
            "SELECT u.* FROM users AS u " +
                    "JOIN friends ff ON (u.id = ff.friend_id) " +
                    "JOIN friends f ON (ff.user_id = f.friend_id) " +
                    "WHERE f.user_id = ? " +
                    "AND ff.friend_id NOT IN " +
                    "(SELECT friend_id FROM friends WHERE u.id = ?) " +
                    "AND ff.friend_id NOT IN " +
                    "(SELECT u.id FROM users AS u JOIN friends AS fr ON (u.id = fr.friend_id) " +
                    "WHERE fr.user_id = ? " +
                    "LIMIT ?, ?)";

    public static final String SQL_FIND_USER_BY_FULL_NAME =
            "SELECT u.id, u.first_name, u.last_name " +
                   "FROM users AS u " +
                   "JOIN friends AS f ON (u.id = f.friend_id) " +
                   "WHERE f.user_id = ? " +
                   "AND u.first_name LIKE '%?%' " +
                   "AND u.last_name LIKE '%?%' " +
                   "LIMIT ?, ?";

    public static final String SQL_FIND_USER_BY_FIRST_OR_LAST_NAME =
            "SELECT u.id, u.first_name, u.last_name " +
                    "FROM users AS u " +
                    "JOIN friends AS f ON (u.id = f.friend_id) " +
                    "WHERE f.user_id = ? " +
                    "AND u.first_name LIKE '%?%' " +
                    "OR u.last_name LIKE '%?%' " +
                    "LIMIT ?, ?";

    public static final String SQL_FIND_FRIEND_REQUEST =
            "SELECT u.* FROM users AS u " +
                    "JOIN friend_requests as fr ON (u.id = fr.user_id) " +
                    "WHERE fr.user_id = ? " +
                    "AND fr.requester_id = ?";

    public static final String SQL_FIND_ALL_FRIEND_REQUESTS_BY_USER =
            "SELECT u.* FROM users AS u " +
                    "JOIN friend_requests as fr ON (u.id = fr.requester_id) " +
                    "WHERE fr.user_id = ? " +
                    "LIMIT ?, ?";

    public static final String SQL_DELETE_MY_PROFILE_FRIENDS =
            "DELETE FROM friends " +
                    "WHERE user_id = ? " +
                    "OR friend_id = ?";

    public static final String SQL_DELETE_MY_PROFILE_FOLLOWERS =
            "DELETE FROM followers " +
                    "WHERE user_id = ? " +
                    "OR follower_id = ? ";

    public static final String SQL_DELETE_FRIEND_BY_ID =
            "DELETE FROM friends " +
                    "WHERE user_id = ? " +
                    "AND friend_id = ?";

    public static final String SQL_DELETE_FOLLOWER_BY_ID =
            "DELETE FROM followers " +
                    "WHERE user_id = ? " +
                    "AND follower_id = ?";

    public static final String SQL_CANCEL_FRIEND_REQUEST =
            "DELETE FROM friend_requests " +
                    "WHERE user_id = ? " +
                    "AND requester_id = ?";

    public List<UserWithoutPassDTO> getFollowerById(long userId, long friendId) {
        return getUserWithoutPassDTOSbyID(friendId, userId, SQL_FIND_FOLLOWER_BY_ID);
    }

    public List<UserWithoutPassDTO> getFriendById(long userId, long friendId) {
        return getUserWithoutPassDTOSbyID(userId, friendId, SQL_FIND_FRIEND_BY_ID);
    }

    public List<UserWithoutPassDTO> findFriendRequest(long userId, long friendId) {
        return getUserWithoutPassDTOSbyID(friendId, userId, SQL_FIND_FRIEND_REQUEST);
    }

    public List<UserWithoutPassDTO> findAllFriendRequestByUserId(long userId, long pageNumber, long rowsNumber) {
      return  getAll(userId, pageNumber, rowsNumber, SQL_FIND_ALL_FRIEND_REQUESTS_BY_USER);
    }

    public List<UserWithoutPassDTO> getAllFriends(long userId, long pageNumber, long rowsNumber) {
        return getAll(userId, pageNumber, rowsNumber, SQL_FIND_ALL_FRIENDS);
    }

    public List<UserWithoutPassDTO> getUserByFullName(String firstName, String lastName,
                                                      long pageNumber, long rowsNumber) {
        return getUserWithoutPassDTOSbyName(firstName, lastName, SQL_FIND_USER_BY_FULL_NAME, pageNumber, rowsNumber);
    }

    public List<UserWithoutPassDTO> getUserByFirstOrLastName(String firstName, String lastName,
                                                             long pageNumber, long rowsNumber) {
        return getUserWithoutPassDTOSbyName(firstName, lastName, SQL_FIND_USER_BY_FIRST_OR_LAST_NAME, pageNumber,
                                                                                                        rowsNumber );
    }
    private List<UserWithoutPassDTO> getAll(long userId, long pageNumber, long rowsNumber, String sql) {
        long skipsNumber = pageNumber * rowsNumber;
        return jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, skipsNumber);
                        ps.setLong(3, rowsNumber);
                    }
                }, (rs, rowNum) -> new UserWithoutPassDTO(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birthday").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("mobile_number"),
                        rs.getString("gender")
                ));
    }

    private List<UserWithoutPassDTO> getUserWithoutPassDTOSbyID(long userId, long friendId, String sql) {
        return jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, friendId);
                    }
                }, (rs, rowNum) -> new UserWithoutPassDTO(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birthday").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("mobile_number"),
                        rs.getString("gender")
                ));
    }

    public List<UserWithoutPassDTO> getFriendsOfMyFriends(long userId, long pageNumber, long rowsNumber) {
        long skipsNumber = pageNumber * rowsNumber;
        return jdbcTemplate.query(SQL_FIND_FRIENDS_OF_MY_FRIENDS,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, userId);
                        ps.setLong(3, userId);
                        ps.setLong(4, skipsNumber);
                        ps.setLong(5, rowsNumber);
                    }
                }, (rs, rowNum) -> new UserWithoutPassDTO(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birthday").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("mobile_number"),
                        rs.getString("gender")
                ));
    }

    public void deleteById(long userId, long friendId, String sql) {
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, friendId);
            }
        });
    }

    public void deleteFollowerById(long userId, long friendId) {
        jdbcTemplate.update(SQL_DELETE_FOLLOWER_BY_ID, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, friendId);
            }
        });
    }

    public void cancelFriendRequest(long userId, long friendId) {
        jdbcTemplate.update(SQL_CANCEL_FRIEND_REQUEST, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, friendId);
                ps.setLong(2, userId);
            }
        });
    }

    public void removeFriendRequest(long userId, long friendId) {
        jdbcTemplate.update(SQL_CANCEL_FRIEND_REQUEST, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, userId);
                ps.setLong(2, friendId);
            }
        });
    }

    private List<UserWithoutPassDTO> getUserWithoutPassDTOSbyName(String firstName, String lastName, String sql,
                                                                                long pageNumber, long rowsNumber) {

        long skipsNumber = pageNumber * rowsNumber;
        return jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setString(1, firstName);
                        ps.setString(2, lastName);
                        ps.setLong(3, skipsNumber);
                        ps.setLong(4, rowsNumber);
                    }
                }, (rs, rowNum) -> new UserWithoutPassDTO(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birthday").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("mobile_number"),
                        rs.getString("gender")
                ));
    }
}
