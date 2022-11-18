package com.example.facebook.controller;

import com.example.facebook.model.dtos.ErrorDTO;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.NotFoundException;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.service.CommentService;
import com.example.facebook.service.PostService;
import com.example.facebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

public abstract class AbstractController{
    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";
    public static final String SHOULD_BE_LOGOUT = "You should be logout. Session terminated.";
    public static final String ALREADY_LOGGED = "The user was already logged in. Session terminated.";
    public static final String YOU_HAVE_TO_LOG_IN_FIRST = "You have to log in first";

    @Autowired
    protected UserService userService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected PostService postService;

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFoundException(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorizedException(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleAllOthers(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ErrorDTO buildErrorInfo(Exception ex, HttpStatus status) {
        ex.printStackTrace();
        ErrorDTO dto = new ErrorDTO();
        dto.setStatus(status.value());
        dto.setTimestamp(LocalDateTime.now());
        dto.setMessage(ex.getMessage());
        return dto;
    }

    protected void logUserAndSetAttribute(HttpSession session, long userId, String userIp) {
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, userId);
        session.setAttribute(REMOTE_ADDRESS, userIp);
    }

    protected long getUserByID(HttpSession session){
        if (session.getAttribute(USER_ID) == null){
            throw new UnauthorizedException(YOU_HAVE_TO_LOG_IN_FIRST);
        }
        return (long) session.getAttribute(USER_ID);
    }

    protected void terminateSession(HttpSession session, String message){
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new UnauthorizedException(message);
        }
    }

    protected void terminateSession(HttpSession session){
        session.invalidate();
    }
}
