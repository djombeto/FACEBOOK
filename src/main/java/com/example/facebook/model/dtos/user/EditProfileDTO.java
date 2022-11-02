package com.example.facebook.model.dtos.user;

import lombok.Data;

import java.time.LocalDate;
@Data
public class EditProfileDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String gender;
}
