package com.example.facebook.model.dtos.user;

import com.example.facebook.model.dtos.post.PostWithoutOwnerDTO;
import com.example.facebook.model.entities.Post;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class NewsFeedDTO {

    private long id;
    private String firstName;
    private String lastName;
    private List<PostWithoutOwnerDTO> newsFeed;
}
