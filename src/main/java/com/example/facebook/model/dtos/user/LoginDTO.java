package com.example.facebook.model.dtos.user;

import lombok.Data;

@Data
public class LoginDTO {

    private String email;
    private String passwordHash;
}
