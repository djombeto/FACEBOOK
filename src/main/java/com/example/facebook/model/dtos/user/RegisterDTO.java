package com.example.facebook.model.dtos.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {

    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirthday;
    private String email;
    private String mobileNumber;
    private String passwordHash;
    private String confirmPasswordHash;
    private String gender;
}
