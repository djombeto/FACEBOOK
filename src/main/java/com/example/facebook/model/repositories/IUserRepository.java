package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    public abstract Optional<User> findByEmail(String email);
    public abstract Optional<User> findByMobileNumber(String number);
}
