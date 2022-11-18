package com.example.facebook.model.entities.user;

import com.example.facebook.model.entities.comment.Comment;
import com.example.facebook.model.entities.comment.CommentReaction;
import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.post.PostReaction;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
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
    private String lastName;
    @Column
    private LocalDate dateOfBirthday;
    @Column
    private String email;
    @Column
    private String mobileNumber;
    @Column
    private String passwordHash;
    @Column
    private String gender;
    @Column
    private String userPhotoUri;

    @OneToMany(mappedBy = "owner")
    private List<Comment> comments;

    @OneToMany(mappedBy = "owner")
    private List<Post> posts;

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> myFriends;

    public void addFriend(User friend){
        myFriends.add(friend);
    }

    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private List<User> myFollowers;

    public void addFollower(User follower){
        myFollowers.add(follower);
    }

    @ManyToMany
    @JoinTable(
            name = "friend_requests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "requester_id"))
    private List<User> myRequests;

    public void addRequester(User requester){
        myRequests.add(requester);
    }

    @OneToMany(mappedBy = "user")
    List<PostReaction> postLikes;

    @OneToMany(mappedBy = "user")
    List<CommentReaction> commentLikes;
}
