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

    private String gender;

    public UserWithoutPassDTO(){

    }
    public UserWithoutPassDTO(long id,
                              String firstName,
                              String lastName,
                              LocalDate dateOfBirthday,
                              String email,
                              String mobileNumber,
                              String gender){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirthday = dateOfBirthday;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
    }
}
