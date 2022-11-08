package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.post.Post;
import com.example.facebook.model.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

     Optional<User> findByEmail(String email);
     Optional<User> findByMobileNumber(String number);

}
