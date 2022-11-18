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
    public ResponseEntity<NewsFeedDTO> login(HttpServletRequest req, @RequestBody LoginDTO dto,
                                                                     @RequestParam long pageNumber,
                                                                     @RequestParam long rowsNumber) {
        HttpSession session = req.getSession();
        terminateSession(session,ALREADY_LOGGED);
        NewsFeedDTO newsFeedDTO = userService.login(dto, pageNumber, rowsNumber);
        long userID = newsFeedDTO.getId();
        String userIP = req.getRemoteAddr();
        logUserAndSetAttribute(session, userID, userIP);
        return new ResponseEntity<>(newsFeedDTO, OK);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session) {
        terminateSession(session);
    }


    @GetMapping("/users/news-feed")
    public ResponseEntity<NewsFeedDTO> newsFeed(@RequestParam long pageNumber,
                                                @RequestParam long rowsNumber,
                                                        HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.newsFeed(userID, pageNumber, rowsNumber), OK);
    }

    @GetMapping("/users/my-profile")
    public ResponseEntity<UserProfileDTO> myProfile(@RequestParam long pageNumber,
                                                    @RequestParam long rowsNumber,
                                                            HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.myProfile(userID, pageNumber, rowsNumber), OK);
    }

    @GetMapping("/users/suggestions")
    public ResponseEntity<List<UserWithoutPassDTO>> friendsSuggestions(@RequestParam long pageNumber,
                                                                       @RequestParam long rowsNumber,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.friendsSuggestions(userID, pageNumber, rowsNumber), OK);
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<List<UserWithoutPassDTO>> findFriendsByName(@PathVariable String name,
                                                                      @RequestParam long pageNumber,
                                                                      @RequestParam long rowsNumber,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.findFriendsByName(userID, name, pageNumber, rowsNumber), OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPassDTO>> findAllFriends(@RequestParam long pageNumber,
                                                                   @RequestParam long rowsNumber,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.findAllFriends(userID, pageNumber, rowsNumber), OK);
    }

    @DeleteMapping("/users")
    public ResponseEntity<ChangePasswordResponseDTO> deleteProfile(HttpSession session) {
        long userID = getUserByID(session);
        terminateSession(session);
        return new ResponseEntity<>(userService.deleteProfile(userID), OK);
    }

    @PutMapping("/users/info")
    public ResponseEntity<EditProfileDTO> editInfo(@RequestBody EditProfileDTO dto, HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.editInfo(dto, userID), OK);
    }

    @PutMapping("/users/password")
    public ResponseEntity<ChangePasswordResponseDTO> editPassword(@RequestBody EditPasswordDTO dto,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        terminateSession(session);
        return new ResponseEntity<>(userService.editPassword(userID, dto), OK);
    }

    @PostMapping("users/friends/{fid}")
    public ResponseEntity<UserWithoutPassDTO> sendFriendRequest(@PathVariable (name = "fid") long friendID,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.sendFriendRequest(userID, friendID), OK);
    }

    @GetMapping("/users/requests")
    public ResponseEntity<List<UserWithoutPassDTO>> findAllFriendRequests(@RequestParam long pageNumber,
                                                                          @RequestParam long rowsNumber,
                                                                               HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.findAllFriendRequests(userID, pageNumber, rowsNumber), OK);
    }

    @PostMapping("/users/{fid}/{confirm}")
    public ResponseEntity<UserWithoutPassDTO> confirmFriendRequest(@PathVariable (name = "fid") long friendId,
                                                                   @PathVariable  String confirm,
                                                                                HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.confirmFriendRequest(userID, friendId, confirm), OK);
    }

    @DeleteMapping("users/friends/{fid}")
    public ResponseEntity<UserWithoutPassDTO> deleteFriend(@PathVariable (name = "fid") long friendID,
                                                                                HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.deleteFriend(userID, friendID), OK);
    }

    @PostMapping("users/follow/{fid}")
    public ResponseEntity<UserWithoutPassDTO> followFriend(@PathVariable (name = "fid") long friendID,
                                                                                HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.followFriend(userID, friendID), OK);
    }

    @PostMapping("users/unfollow/{fid}")
    public ResponseEntity<UserWithoutPassDTO> unfollowFriend(@PathVariable (name = "fid") long friendID,
                                                                                 HttpSession session) {
        long userID = getUserByID(session);
        return new ResponseEntity<>(userService.unfollowFriend(userID, friendID), OK);
    }
}
