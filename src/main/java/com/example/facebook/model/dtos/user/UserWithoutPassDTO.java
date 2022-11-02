package com.example.facebook.model.dtos.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserWithoutPassDTO {

    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirthday;
    private String email;
    private String mobileNumber;
}
