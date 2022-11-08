package com.example.facebook.model.dtos.user;

import lombok.Data;
import lombok.Setter;

@Data
public class FriendDTO {

    private long id;
    private String firstName;
    private String lastName;

    public FriendDTO(){

    }
    public FriendDTO(long id){
        this.id = id;
    }
    public FriendDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public FriendDTO(long id, String firstName, String lastName){
        this(firstName, lastName);
        this.id = id;
    }
}
