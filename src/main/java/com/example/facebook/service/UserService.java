package com.example.facebook.service;

import com.example.facebook.model.dtos.user.*;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.repositories.AbstractRepositories;
import com.example.facebook.model.utility.RegexValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractRepositories {

    public UserWithoutPassDTO register(RegisterDTO dto) {
        validateRegisterDTO(dto);
        User user = modelMapper.map(dto, User.class);
        user.setPasswordHash(encoder.encode(dto.getPasswordHash()));
        userRepository.save(user);
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

    public List<FriendDTO> findFriendsByName(long userId, String name) {
        validateUser(userId);
        String firstName = name;
        String lastName = name;
        String[] names = name.split("\\s+");
        if (names.length > 1) {
            firstName = names[0];
            lastName = names[1];
            return jdbcTemplate.query(
                    "SELECT u.id, u.first_name, u.last_name FROM users AS u " +
                            "JOIN friends AS f ON (u.id = f.friend_id) " +
                            "WHERE f.user_id = " + userId + " " +
                            "AND u.first_name LIKE '" + firstName + "%' " +
                            "AND u.last_name LIKE '" + lastName + "%'",
                    (rs, rowNum) -> new FriendDTO(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")));
        } else {
            return jdbcTemplate.query(
                    "SELECT u.id, u.first_name, u.last_name FROM users AS u " +
                            "JOIN friends AS f ON (u.id = f.friend_id) " +
                            "WHERE f.user_id = " + userId + " " +
                            "AND u.first_name LIKE '" + firstName + "%' " +
                            "OR u.last_name LIKE '" + lastName + "%'",
                    (rs, rowNum) -> new FriendDTO(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")));
        }
    }

    public List<FriendDTO> findAllFriends(long userId) {
        User user = validateUser(userId);
        List<User> users = user.getMyFriends();
        return users
                .stream()
                .map(u -> modelMapper.map(u, FriendDTO.class))
                .collect(Collectors.toList());
    }

    public NewsFeedDTO login(LoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPasswordHash();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnauthorizedException("Invalid email or password");
        });
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        long userId = user.getId();
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(newsFeedQuery(userId));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long postId = e.getPostId();
            e.setComments(commentsQuery(postId));
        });
        return newsFeedDTO;
    }

    public NewsFeedDTO newsFeed(long userId) {
        User user = validateUser(userId);
        NewsFeedDTO newsFeedDTO = modelMapper.map(user, NewsFeedDTO.class);
        newsFeedDTO.setNewsFeed(newsFeedQuery(userId));
        newsFeedDTO.getNewsFeed().forEach(e -> {
            long postId = e.getPostId();
            e.setComments(commentsQuery(postId));
        });
        return newsFeedDTO;
    }

    public UserProfileDTO myProfile(long userId) {
        User user = validateUser(userId);
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        profileDTO.setMyPosts(myPostsQuery(userId));
        profileDTO.getMyPosts().forEach(n -> {
            long postId = n.getPostId();
            n.setComments(commentsQuery(postId));
        });
        return profileDTO;
    }


    public EditProfileDTO editInfo(EditProfileDTO dto, long userId) {
        validateEditProfileDTO(dto);
        User user = validateUser(userId);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setGender(dto.getGender());
        userRepository.save(user);
        return modelMapper.map(user, EditProfileDTO.class);
    }

    public void deleteProfile(long userId) {
        User user = validateUser(userId);
        jdbcTemplate.execute("DELETE FROM friends WHERE user_id = " + userId + " " +
                "AND friend_id = " + userId);

        jdbcTemplate.execute("DELETE FROM followers WHERE user_id = " + userId + " " +
                "AND follower_id = " + userId);

        userRepository.deleteById(userId);
    }

    public FriendDTO addFriend(long userId, long friendId) {
        User user = validateUser(userId);
        User friend = validateUser(friendId);
        if (!jdbcTemplate.query(
                "SELECT * FROM friends " +
                    "WHERE user_id = " + userId + " " +
                    "AND friend_id = " + friendId,
                (rs, rowNum) -> new FriendDTO(
                        rs.getLong("friend_id"))).isEmpty()
        ) {
            throw new BadRequestException("You are already friends");
        }
        user.addFriend(friend);
        friend.addFriend(user);
        userRepository.save(user);
        followFriend(userId,friendId);
        followFriend(friendId, userId);
        return modelMapper.map(friend, FriendDTO.class);
    }

    public void deleteFriend(long userId, long friendId) {
        if (jdbcTemplate.query(
                "SELECT * FROM friends " +
                    "WHERE user_id = " + userId + " " +
                    "AND friend_id = " + friendId,
                (rs, rowNum) -> new FriendDTO(
                        rs.getLong("friend_id"))).isEmpty()
        ) {
            throw new BadRequestException("You don't have a friend with this id");
        } else {
            jdbcTemplate.execute(
                    "DELETE FROM friends " +
                        "WHERE user_id = " + userId + " " +
                        "AND friend_id = " + friendId);

            jdbcTemplate.execute(
                    "DELETE FROM friends " +
                        "WHERE user_id = " + friendId + " " +
                        "AND friend_id = " + userId);

            unfollowFriend(userId,friendId);
            unfollowFriend(friendId, userId);
        }
    }

    public FriendDTO followFriend(long userId, long friendId) {
        User user = validateUser(userId);
        User friend = validateUser(friendId);
        friend.addFollower(user);
        userRepository.save(user);
        return modelMapper.map(friend, FriendDTO.class);
    }

    public void unfollowFriend(long userId, long friendId) {
        User user = validateUser(userId);
        User friend = validateUser(friendId);
        friend.getMyFollowers().remove(user);
        jdbcTemplate.execute("DELETE FROM followers " +
                                 "WHERE user_id = " + friendId + " " +
                                 "AND follower_id = " +  userId);
    }

    public void editPassword(long userId, EditPasswordDTO dto) {
        validateEditPasswordDTO(dto);
        User user = validateUser(userId);
        if (!encoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid password");
        }
        user.setPasswordHash(encoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    private void validateRegisterDTO(RegisterDTO dto) {

        if (RegexValidator.patternNames(dto.getFirstName())) {
            throw new BadRequestException("Invalid first name");
        }
        if (RegexValidator.patternNames(dto.getLastName())) {
            throw new BadRequestException("Invalid last name");
        }
        if (RegexValidator.patternEmails(dto.getEmail())) {
            throw new BadRequestException("Invalid email");
        }
        if (!dto.getGender().equals("m") && !dto.getGender().equals("f") && !dto.getGender().equals("o")) {
            throw new BadRequestException("The gender is not valid.");
        }
        if (!RegexValidator.patternDate(dto.getDateOfBirthday())) {
            throw new BadRequestException("Invalid date");
        }
        validateDateOfBirth(dto.getDateOfBirthday());

        if (RegexValidator.patternPassword(dto.getPasswordHash())) {
            throw new BadRequestException("The password is not secure");
        }
        if (!dto.getPasswordHash().equals(dto.getConfirmPasswordHash())) {
            throw new BadRequestException("The passwords do not match");
        }
        if (RegexValidator.patternPhoneNumber(dto.getMobileNumber())) {
            throw new BadRequestException("Invalid phone number");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("An user with this email has already been registered.");
        }
        if (userRepository.findByMobileNumber(dto.getMobileNumber()).isPresent()) {
            throw new BadRequestException("An user with this mobile number " +
                    "has already been registered.");
        }
    }

    private void validateEditProfileDTO(EditProfileDTO dto) {
        if (RegexValidator.patternNames(dto.getFirstName())) {
            throw new BadRequestException("Invalid first name");
        }
        if (RegexValidator.patternNames(dto.getLastName())) {
            throw new BadRequestException("Invalid last name");
        }
        if (RegexValidator.patternEmails(dto.getEmail())) {
            throw new BadRequestException("Invalid email");
        }
        if (RegexValidator.patternPhoneNumber(dto.getMobileNumber())) {
            throw new BadRequestException("Invalid phone number");
        }
    }

    private void validateEditPasswordDTO(EditPasswordDTO dto) {
        if (RegexValidator.patternPassword(dto.getNewPassword())) {
            throw new BadRequestException("The password is not secure");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new BadRequestException("The passwords do not match");
        }
    }

    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new BadRequestException("The date of birth is blank.");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("The date of birth is after the current date.");
        }
        long years = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
        if (years < 18) {
            throw new BadRequestException("Too young.");
        }
        if (years > 150) {
            throw new BadRequestException("Too old to be alive.");
        }
    }
}
