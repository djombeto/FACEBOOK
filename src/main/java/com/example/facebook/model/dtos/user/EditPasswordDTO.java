package com.example.facebook.model.dtos.user;

import lombok.Data;

@Data
public class EditPasswordDTO extends AbstractMasterDTO {

    private String oldPassword;
    private String passwordHash; // new password
    private String confirmPasswordHash; // confirm new password
}
