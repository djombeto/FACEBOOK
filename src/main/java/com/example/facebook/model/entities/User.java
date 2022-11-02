package com.example.facebook.model.entities;

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

    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private List<User> myFollowers;

    public void addFriend(User friend){
        myFriends.add(friend);
    }

    public void addFollower(User follower){
        myFollowers.add(follower);
    }

}
