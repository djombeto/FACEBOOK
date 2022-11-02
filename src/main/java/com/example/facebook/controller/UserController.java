package com.example.facebook.controller;

import com.example.facebook.model.dtos.user.*;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController extends AbstractController {

    @PostMapping("/users/register")
    public ResponseEntity<UserWithoutPassDTO> register(HttpSession session,
                                                       @RequestBody RegisterDTO dto) {
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new UnauthorizedException("You should be logout. Session terminated.");
        }
        return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity<NewsFeedDTO> login(HttpServletRequest req, @RequestBody LoginDTO dto) {
        HttpSession session = req.getSession();
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new BadRequestException("The user was already logged in. Session terminated.");
        }
        NewsFeedDTO newsFeedDTO = userService.login(dto);
        long userId = newsFeedDTO.getId();
        String userIp = req.getRemoteAddr();
        logUserAndSetAttribute(session, userId, userIp);
        return new ResponseEntity<>(newsFeedDTO, HttpStatus.OK);
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/users/newsfeed")
    public ResponseEntity<NewsFeedDTO> newsFeed(HttpSession session) {
        long userId = getUserId(session);
        return new ResponseEntity<>(userService.newsFeed(userId), HttpStatus.OK);
    }

    @GetMapping("/users/myProfile")
    public ResponseEntity<UserProfileDTO> myProfile(HttpSession session) {
        long userId = getUserId(session);
        return new ResponseEntity<>(userService.myProfile(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<List<FriendDTO>> findFriendsByName(HttpSession session,
                                                             @PathVariable String name) {
        long userId = getUserId(session);
        return new ResponseEntity<>(userService.findFriendsByName(userId, name), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<FriendDTO>> findAllFriends(HttpSession session) {
        long userId = getUserId(session);
        return new ResponseEntity<>(userService.findAllFriends(userId), HttpStatus.OK);
    }

    @DeleteMapping("/users")
    public void deleteProfile(HttpSession session) {
        long userId = getUserId(session);
        session.invalidate();
        userService.deleteProfile(userId);
    }

    @PutMapping("/users/edit/info")
    public ResponseEntity<EditProfileDTO> editInfo(HttpSession session,
                                                   @RequestBody EditProfileDTO dto) {
        long userId = getUserId(session);
        return new ResponseEntity<>(userService.editInfo(dto, userId), HttpStatus.OK);
    }

    @PutMapping("/users/edit/password")
    public void editPassword(HttpSession session, @RequestBody EditPasswordDTO dto) {
        long userId = getUserId(session);
        userService.editPassword(userId, dto);
    }

    @PostMapping("users/add/{fid}")
    public ResponseEntity<FriendDTO> addFriend(HttpSession session,
                                               @PathVariable (name = "fid") long friendId) {
        long userId = getUserId(session);
        compareBothId(userId, friendId, "You cannot add yourself");
        return new ResponseEntity<>(userService.addFriend(userId, friendId), HttpStatus.OK);
    }

    @DeleteMapping("users/delete/{fid}")
    public void deleteFriend(HttpSession session, @PathVariable (name = "fid") long friendId) {
        long userId = getUserId(session);
        compareBothId(userId, friendId,"You cannot delete yourself");
        userService.deleteFriend(userId, friendId);
    }

    @PostMapping("users/follow/{fid}")
    public ResponseEntity<FriendDTO> followFriend(HttpSession session,
                                                  @PathVariable (name = "fid") long friendId) {
        long userId = getUserId(session);
        compareBothId(userId, friendId, "You cannot follow yourself" );
        return new ResponseEntity<>(userService.followFriend(userId, friendId), HttpStatus.OK);
    }

    @PostMapping("users/unfollow/{fid}")
    public void unfollowFriend(HttpSession session,
                                                  @PathVariable (name = "fid") long friendId) {
        long userId = getUserId(session);
        compareBothId(userId, friendId, "You cannot unfollow yourself" );
        userService.unfollowFriend(userId, friendId);
    }
}
