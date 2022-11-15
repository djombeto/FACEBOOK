package com.example.facebook.controller;

import com.example.facebook.model.dtos.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class UserController extends AbstractController {


    @PostMapping("/users/register")
    public ResponseEntity<UserWithoutPassDTO> register(HttpSession session, @RequestBody RegisterDTO dto) {
        terminateSession(session, SHOULD_BE_LOGOUT);
        return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity<NewsFeedDTO> login(HttpServletRequest req, @RequestBody LoginDTO dto) {
        HttpSession session = req.getSession();
        terminateSession(session,ALREADY_LOGGED);
        NewsFeedDTO newsFeedDTO = userService.login(dto);
        long userID = newsFeedDTO.getId();
        String userIP = req.getRemoteAddr();
        logUserAndSetAttribute(session, userID, userIP);
        return new ResponseEntity<>(newsFeedDTO, OK);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session) {
        terminateSession(session);
    }


    @GetMapping("/users/newsfeed")
    public ResponseEntity<NewsFeedDTO> newsFeed(HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.newsFeed(userID), OK);
    }

    @GetMapping("/users/myProfile")
    public ResponseEntity<UserProfileDTO> myProfile(HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.myProfile(userID), OK);
    }

    @GetMapping("/users/suggestions")
    public ResponseEntity<List<UserWithoutPassDTO>> friendsSuggestions(HttpSession session){
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.friendsSuggestions(userID), OK);
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<List<UserWithoutPassDTO>> findFriendsByName(HttpSession session, @PathVariable String name) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.findFriendsByName(userID, name), OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPassDTO>> findAllFriends(HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.findAllFriends(userID), OK);
    }

    @DeleteMapping("/users")
    public ResponseEntity<ChangePasswordResponseDTO> deleteProfile(HttpSession session) {
        long userID = getUserByID(session);
        terminateSession(session);
        return new ResponseEntity<>(userService.deleteProfile(userID), OK);
    }

    @PutMapping("/users/info")
    public ResponseEntity<EditProfileDTO> editInfo(HttpSession session, @RequestBody EditProfileDTO dto) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.editInfo(dto, userID), OK);
    }

    @PutMapping("/users/password")
    public ResponseEntity<ChangePasswordResponseDTO> editPassword(HttpSession session,
                                                                  @RequestBody EditPasswordDTO dto) {
        long userID = getUserByID(session);
        terminateSession(session);
        return new ResponseEntity<>(userService.editPassword(userID, dto), OK);
    }

    @PostMapping("users/friends/{fid}")
    public ResponseEntity<UserWithoutPassDTO> addFriend(HttpSession session, @PathVariable (name = "fid") long friendID) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.addFriend(userID, friendID), OK);
    }

    @DeleteMapping("users/friends/{fid}")
    public ResponseEntity<UserWithoutPassDTO> deleteFriend(HttpSession session, @PathVariable (name = "fid")
                                                                                              long friendID) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.deleteFriend(userID, friendID), OK);
    }

    @PostMapping("users/follow/{fid}")
    public ResponseEntity<UserWithoutPassDTO> followFriend(HttpSession session, @PathVariable (name = "fid") long friendID) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.followFriend(userID, friendID), OK);
    }

    @PostMapping("users/unfollow/{fid}")
    public ResponseEntity<UserWithoutPassDTO> unfollowFriend(HttpSession session, @PathVariable (name = "fid")
                                                                                                 long friendID) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.unfollowFriend(userID, friendID), OK);
    }
}
