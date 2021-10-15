package com.gostokhov.chat.repository;

import com.gostokhov.chat.domain.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    List<User> findByUsernameContains(String searchString);
}
