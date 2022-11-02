package com.example.facebook.model.dtos.user;

import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO {

    private int id;
    private String firstName;
    private String lastName;
    private List<PostWithoutOwnerDTO> myPosts;
   // private String userPhotoUri;
}
