package com.example.facebook.controller;

import com.example.facebook.model.dtos.ErrorDTO;
import com.example.facebook.model.exceptions.BadRequestException;
import com.example.facebook.model.exceptions.NotFoundExceptions;
import com.example.facebook.model.exceptions.UnauthorizedException;
import com.example.facebook.service.AbstractService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

public abstract class AbstractController extends AbstractService {
    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundExceptions.class)
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

    public void logUserAndSetAttribute(HttpSession session, long userId, String userIp) {
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, userId);
        session.setAttribute(REMOTE_ADDRESS, userIp);
    }

    public long getUserById(HttpSession session){
        if (session.getAttribute(USER_ID) == null){
            return 0;
        }
        return (long) session.getAttribute(USER_ID);
    }
}
