package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserById(Long id);

    List<User> findByUsernameContains(String searchString);
}
