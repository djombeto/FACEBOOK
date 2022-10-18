package com.example.facebook.model.repositories;

import com.example.facebook.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}
