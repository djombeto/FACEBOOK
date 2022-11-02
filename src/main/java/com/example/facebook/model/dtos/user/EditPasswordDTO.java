package com.example.facebook.model.dtos.user;

import lombok.Data;

@Data
public class EditPasswordDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
