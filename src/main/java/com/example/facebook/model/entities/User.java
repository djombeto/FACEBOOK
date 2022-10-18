package com.example.facebook.model.entities;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String firstName;
    @Column
    private String surname;
    @Column
    private String dateOfBirthday;
    @Column
    private String email;
    @Column
    private String mobilePhone;
    @Column
    private String password;
}
