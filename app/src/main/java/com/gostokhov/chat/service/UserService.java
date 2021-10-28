package com.gostokhov.chat.service;

import com.gostokhov.chat.domain.JwtToken;
import com.gostokhov.chat.dto.user.UserCredentialsDto;
import com.gostokhov.chat.dto.user.UserRegisterDto;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.exception.domain.EmailExistException;
import com.gostokhov.chat.exception.domain.EmailNotFoundException;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {
    JwtToken generateJwtTokenPojo(UserCredentialsDto userCredentialsDto) throws UserNotFoundException;

    void register(UserRegisterDto userRegisterDto) throws UserNotFoundException, EmailExistException, UsernameExistException;

    User getCurrentUser() throws UserNotFoundException;

    List<User> getUsers();

    List<User> getUsersByUsername(String searchString);

    User findUserByUsername(String username) throws UserNotFoundException;

    User findUserByEmail(String email) throws UserNotFoundException;

    User findUserById(Long id) throws UserNotFoundException;

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newEmail, String newUsername, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    Set<User> validateUsernameList(Set<String> usernameList) throws UserNotFoundException;
}
