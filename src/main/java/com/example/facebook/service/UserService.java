package com.example.facebook.service;

import com.example.facebook.model.daos.UserDAO;
import com.example.facebook.model.dtos.user.*;
import com.example.facebook.model.entities.user.User;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.model.utility.RegexValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserService extends AbstractService {

    public static final String CANNOT_ADD = "You cannot add yourself";
    public static final String CANNOT_REQUEST = "You cannot send a friend request to yourself";
    public static final String CANNOT_FOLLOW = "You cannot follow yourself";
    public static final String CANNOT_UNFOLLOW = "You cannot unfollow yourself";
    public static final String CANNOT_DELETE = "You cannot delete yourself";
    public static final String CHANGE_PASSWORD = "Changing password is complete";
    public static final String DELETE_PROFILE = "Your profile is deleted";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String INVALID_PASSWORD = "Invalid password";
    private static final String UNEXISTEND_FRIEND = "You don't have a friend with this id";
    private static final String ALREADY_FRIENDS = "You are already friends";
    private static final String ALREADY_FOLLOW = "You already follow this user";

    public UserWithoutPassDTO register(RegisterDTO dto) {
        validateFirsLastName(dto);
        validateDateOfBirth(dto);
        validateMobileNumber(dto);
        validateEmail(dto);
        validateGender(dto);
        validatePassword(dto);
        User user = modelMapper.map(dto, User.class);
        user.setPasswordHash(encoder.encode(dto.getPasswordHash()));
        userRepository.save(user);
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

    public NewsFeedDTO login(LoginDTO dto, long pageNumber, long rowsNumber) {
        String email = dto.getEmail();
        String password = dto.getPasswordHash();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnauthorizedException(INVALID_EMAIL_OR_PASSWORD);
        });
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException(INVALID_EMAIL_OR_PASSWORD);
        }
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    public NewsFeedDTO newsFeed(long userId, long pageNumber, long rowsNumber) {
        User user = verifyUser(userId);
        return showNewsFeed(user, pageNumber, rowsNumber);
    }

    public UserProfileDTO myProfile(long userId, long pageNumber, long rowsNumber) {
        User user = verifyUser(userId);
        return showMyPosts(user, pageNumber, rowsNumber);
    }

    public List<UserWithoutPassDTO> friendsSuggestions(long userId, long pageNumber, long rowsNumber) {
        verifyUser(userId);
        return userDAO.getFriendsOfMyFriends(userId, pageNumber, rowsNumber);
    }

    public EditProfileDTO editInfo(EditProfileDTO dto, long userId) {
        User user = verifyUser(userId);
        validateFirsLastName(dto);
        validateEmail(dto);
        validateMobileNumber(dto);
        setNewValues(dto, user);
        userRepository.save(user);
        return modelMapper.map(user, EditProfileDTO.class);
    }

    public ChangePasswordResponseDTO editPassword(long userId, EditPasswordDTO dto) {
        User user = verifyUser(userId);
        if (!encoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(INVALID_PASSWORD);
        }
        validatePassword(dto);
        user.setPasswordHash(encoder.encode(dto.getPasswordHash()));
        userRepository.save(user);
        return new ChangePasswordResponseDTO(LocalDateTime.now(), CHANGE_PASSWORD);
    }

    @Transactional
    public ChangePasswordResponseDTO deleteProfile(long userID) {
        verifyUser(userID);
        userDAO.deleteById(userID, userID, UserDAO.SQL_DELETE_MY_PROFILE_FRIENDS);
        userDAO.deleteById(userID, userID, UserDAO.SQL_DELETE_MY_PROFILE_FOLLOWERS);
        userRepository.deleteById(userID);
        return new ChangePasswordResponseDTO(LocalDateTime.now(), DELETE_PROFILE);
    }

    public UserWithoutPassDTO sendFriendRequest(long userId, long friendId) {
        compareBothId(userId, friendId, CANNOT_REQUEST);
        User user = verifyUser(userId);
        User friend = verifyUser(friendId);
        if (thereAlreadyARequest(userId, friendId)) {
            userDAO.cancelFriendRequest(userId, friendId);
        } else {
            friend.addRequester(user);
            userRepository.save(friend);
            return modelMapper.map(friend, UserWithoutPassDTO.class);
        }
        return null;
    }

    @Transactional
    public UserWithoutPassDTO deleteFriend(long userId, long friendId) {
        compareBothId(userId, friendId, CANNOT_DELETE);
        if (userDAO.getFriendById(userId, friendId).isEmpty()) {
            throw new BadRequestException(UNEXISTEND_FRIEND);
        } else {
            userDAO.deleteById(userId, friendId, UserDAO.SQL_DELETE_FRIEND_BY_ID);
            userDAO.deleteById(friendId, userId, UserDAO.SQL_DELETE_FRIEND_BY_ID);
            unfollowFriend(userId, friendId);
            unfollowFriend(friendId, userId);
            return modelMapper.map(verifyUser(friendId), UserWithoutPassDTO.class);
        }
    }

    public UserWithoutPassDTO followFriend(long userId, long friendId) {
        compareBothId(userId, friendId, CANNOT_FOLLOW);
        if (userDAO.getFollowerById(userId, friendId).size() > 0) {
            throw new BadRequestException(ALREADY_FOLLOW);
        }
        User user = verifyUser(userId);
        User friend = verifyUser(friendId);
        friend.addFollower(user);
        userRepository.save(user);
        return modelMapper.map(friend, UserWithoutPassDTO.class);
    }

    public UserWithoutPassDTO unfollowFriend(long userId, long friendId) {
        compareBothId(userId, friendId, CANNOT_UNFOLLOW);
        User user = verifyUser(userId);
        User friend = verifyUser(friendId);
        friend.getMyFollowers().remove(user);
        userDAO.deleteFollowerById(friendId, userId);
        return modelMapper.map(verifyUser(friendId), UserWithoutPassDTO.class);
    }

    public List<UserWithoutPassDTO> findFriendsByName(long userId, String name, long pageNumber, long rowsNumber) {
        verifyUser(userId);
        String firstName = name;
        String lastName = name;
        String[] names = name.split("\\s+");
        if (names.length > 1) {
            firstName = names[0];
            lastName = names[1];
            return userDAO.getUserByFullName(firstName, lastName, pageNumber, rowsNumber);
        } else {
            return userDAO.getUserByFirstOrLastName(firstName, lastName, pageNumber, rowsNumber);
        }
    }

    @Transactional
    public UserWithoutPassDTO confirmFriendRequest(long userId, long friendId, String confirm) {
        compareBothId(userId, friendId, CANNOT_ADD);
        User user = verifyUser(userId);
        User friend = verifyUser(friendId);
        if (youAreAlreadyFriends(userId, friendId)){
            throw new BadRequestException(ALREADY_FRIENDS);
        }
        switch (confirm.toUpperCase()){
            case "CONFIRM":
                user.addFriend(friend);
                friend.addFriend(user);
                followFriend(userId, friendId);
                followFriend(friendId, userId);
                userDAO.removeFriendRequest(userId, friendId);
                userRepository.save(user);
                return modelMapper.map(friend, UserWithoutPassDTO.class);
            case "REMOVE":
                userDAO.removeFriendRequest(userId, friendId);
                return modelMapper.map(friend, UserWithoutPassDTO.class);
            default:
                throw new BadRequestException("Invalid confirmation");
        }
    }

    public List<UserWithoutPassDTO> findAllFriends(long userId, long pageNumber, long rowsNumber) {
        verifyUser(userId);
        return userDAO.getAllFriends(userId, pageNumber, rowsNumber);
    }

    public List<UserWithoutPassDTO> findAllFriendRequests(long userID, long pageNumber, long rowsNumber) {
        verifyUser(userID);
        return userDAO.findAllFriendRequestByUserId(userID, pageNumber, rowsNumber);
    }

    private static void setNewValues(EditProfileDTO dto, User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setGender(dto.getGender());
    }

    private void validateFirsLastName(AbstractMasterDTO dto) {
        if (RegexValidator.patternNames(dto.getFirstName())) {
            throw new BadRequestException("Invalid first name");
        }
        if (RegexValidator.patternNames(dto.getLastName())) {
            throw new BadRequestException("Invalid last name");
        }
    }

    private void validateMobileNumber(AbstractMasterDTO dto) {
        if (RegexValidator.patternPhoneNumber(dto.getMobileNumber())) {
            throw new BadRequestException("Invalid phone number");
        }

        if (userRepository.findByMobileNumber(dto.getMobileNumber()).isPresent()) {
            throw new BadRequestException("An user with this mobile number " +
                    "has already been registered.");
        }
    }

    private void validateEmail(AbstractMasterDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("An user with this email has already been registered.");
        }
        if (RegexValidator.patternEmails(dto.getEmail())) {
            throw new BadRequestException("Invalid email");
        }
    }

    private void validateGender(AbstractMasterDTO dto) {
        if (!dto.getGender().equals("m") && !dto.getGender().equals("f") && !dto.getGender().equals("o")) {
            throw new BadRequestException("The gender is not valid.");
        }
    }

    private void validatePassword(AbstractMasterDTO dto) {
        if (RegexValidator.patternPassword(dto.getPasswordHash())) {
            throw new BadRequestException("The password is not secure");
        }
        if (!dto.getPasswordHash().equals(dto.getConfirmPasswordHash())) {
            throw new BadRequestException("The passwords do not match");
        }
    }

    private void validateDateOfBirth(AbstractMasterDTO dto) {
        if (dto.getDateOfBirthday() == null) {
            throw new BadRequestException("The date of birth is blank.");
        }
        if (RegexValidator.patternDate(dto.getDateOfBirthday())) {
            throw new BadRequestException("Invalid date");
        }
        if (dto.getDateOfBirthday().isAfter(LocalDate.now())) {
            throw new BadRequestException("The date of birth is after the current date.");
        }
        long years = ChronoUnit.YEARS.between(dto.getDateOfBirthday(), LocalDate.now());
        if (years < 18) {
            throw new BadRequestException("Too young.");
        }
        if (years > 150) {
            throw new BadRequestException("Too old to be alive.");
        }
    }

    private void compareBothId(long userId, long friendId, String message) {
        if (userId == friendId) {
            throw new BadRequestException(message);
        }
    }

    private boolean thereAlreadyARequest(long userId, long friendId){
        return userDAO.findFriendRequest(userId, friendId).size() > 0;
    }

    private boolean youAreAlreadyFriends(long userId, long friendId){
        return userDAO.getFriendById(userId, friendId).size() > 0;
    }

}
