package com.gostokhov.chat.service;

import com.gostokhov.chat.domain.User;
import com.gostokhov.chat.exception.domain.EmailExistException;
import com.gostokhov.chat.exception.domain.EmailNotFoundException;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String email, String username, String password) throws UserNotFoundException, EmailExistException, UsernameExistException;

    List<User> getUsers();

    List<User> getUsersBySearchString(String searchString);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newEmail, String newUsername, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
}
